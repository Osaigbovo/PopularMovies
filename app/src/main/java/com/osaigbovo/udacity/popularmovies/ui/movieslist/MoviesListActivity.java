/*
 * Copyright 2018.  Osaigbovo Odiase
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.osaigbovo.udacity.popularmovies.PopularMoviesApp;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.Status;
import com.osaigbovo.udacity.popularmovies.ui.base.BaseActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.util.AppConstants;
import com.osaigbovo.udacity.popularmovies.util.RetryCallback;
import com.osaigbovo.udacity.popularmovies.util.SharedPreferenceUtils;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of movies, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * movie details. On tablets, the activity presents the list of movies and
 * movie details side-by-side using two vertical panes.
 */
public class MoviesListActivity extends BaseActivity implements RetryCallback {

    private static final String SAVED_LAYOUT_MANAGER = "layout-manager-state";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MoviesListViewModel moviesListViewModel;

    @BindView(R.id.moviesSwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_movies_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.errorMessageTextView)
    TextView mErrorMessageTextView;
    @BindView(R.id.retryLoadingButton)
    Button mRetryButton;
    @BindView(R.id.loadingProgressBar)
    ProgressBar mProgressBar;
    @BindInt(R.integer.movies_columns)
    int mColumns;
    @BindDimen(R.dimen.grid_item_spacing)
    int mGridSpacing;

    private GridLayoutManager gridLayoutManager;
    private MoviesListAdapter moviesListAdapter;
    private Parcelable mLayoutManagerSavedState;

    // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        moviesListViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MoviesListViewModel.class);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);
        initSwipeToRefresh();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        gridLayoutManager = new GridLayoutManager(this, mColumns);
        moviesListAdapter = new MoviesListAdapter(this, this, mTwoPane);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(moviesListAdapter);

        loadMoviesFromSharedPrefs();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRecyclerView != null) {
            postponeEnterTransition();
            mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mRecyclerView.requestLayout();
                    startPostponedEnterTransition();
                    return true;
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //moviesListViewModel.moviesList.removeObservers(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem sort) {
        int id = sort.getItemId();
        if (id == R.id.action_sort) {
            sortMovies();
            return true;
        }
        return super.onOptionsItemSelected(sort);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Init swipe to refresh and enable pull to refresh only when there are items in the adapter
     */
    private void initSwipeToRefresh() {
        moviesListViewModel.getRefreshState().observe(this, networkState -> {
            mSwipeRefreshLayout.setRefreshing(networkState == NetworkState.LOADING);

        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> moviesListViewModel.refresh());
        // Scheme colors for animation
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
    }

    private void loadMoviesFromSharedPrefs() {
        int loadingIdentifier = SharedPreferenceUtils.contains(AppConstants.PREF_FILTER) ? 2 : 1;

        switch (loadingIdentifier) {
            case 1:
                SharedPreferenceUtils
                        .setSharedPreferenceString(AppConstants.PREF_FILTER, AppConstants.SORT_BY_POPULAR);
                loadMovies(AppConstants.SORT_BY_POPULAR);
                break;
            case 2:
                if (SharedPreferenceUtils
                        .getSharedPreferenceString(AppConstants.PREF_FILTER, null)
                        .equals(AppConstants.SORT_BY_TOP_RATED)) {
                    loadMovies(AppConstants.SORT_BY_TOP_RATED);

                } else if (SharedPreferenceUtils
                        .getSharedPreferenceString(AppConstants.PREF_FILTER, null)
                        .equals(AppConstants.SORT_BY_POPULAR)) {
                    loadMovies(AppConstants.SORT_BY_POPULAR);
                }
                break;
            default:
                break;
        }
    }

    // BottomSheetDialog. Fetch movies based on selection.
    private void sortMovies() {
        View view = View.inflate(this, R.layout.bottom_sheet_sort, null);

        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        Objects.requireNonNull(mBottomSheetDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mBottomSheetDialog.setContentView(view);

        ImageView mImageClose = view.findViewById(R.id.image_close);
        RadioGroup mRadioGroup = view.findViewById(R.id.radio_group_sort);
        RadioButton mRadioButtonPopular = view.findViewById(R.id.radio_button_popular);
        RadioButton mRadioButtonTopRated = view.findViewById(R.id.radio_button_top_rated);

        switch (SharedPreferenceUtils.getSharedPreferenceString(AppConstants.PREF_FILTER, null)) {
            case AppConstants.SORT_BY_TOP_RATED:
                mRadioButtonTopRated.setChecked(true);
                break;
            case AppConstants.SORT_BY_POPULAR:
                mRadioButtonPopular.setChecked(true);
                break;
        }

        mRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            mBottomSheetDialog.dismiss();
            switch (checkedId) {
                case R.id.radio_button_popular:
                    if (PopularMoviesApp.hasNetwork()) {
                        SharedPreferenceUtils.setSharedPreferenceString(AppConstants.PREF_FILTER, AppConstants.SORT_BY_POPULAR);
                        Timber.i("Get Popular Movies");
                        loadMovies(AppConstants.SORT_BY_POPULAR);
                    } else {
                        //AppUtils.setSnackBar(snackBarView, getString(R.string.error_no_internet));
                    }
                    break;
                case R.id.radio_button_top_rated:
                    if (PopularMoviesApp.hasNetwork()) {
                        SharedPreferenceUtils.setSharedPreferenceString(AppConstants.PREF_FILTER, AppConstants.SORT_BY_TOP_RATED);
                        Timber.i("Get Top Rated Movies");
                        loadMovies(AppConstants.SORT_BY_TOP_RATED);
                    } else {
                        //AppUtils.setSnackBar(snackBarView, getString(R.string.error_no_internet));
                    }
                    break;
                default:
                    break;
            }
        });

        mImageClose.setOnClickListener(view1 -> mBottomSheetDialog.dismiss());
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.show();
    }

    // Pass require filter/sort
    private void loadMovies(String sort) {
        moviesListViewModel.sort(sort);
        setupList();
    }

    // Fetch and observe List. Also submit paged list to Adapter.
    public void setupList() {
        moviesListViewModel.moviesList.observe(this, moviesListAdapter::submitList);
        moviesListViewModel.getNetworkState().observe(this, moviesListAdapter::setNetworkState);
    }

    /**
     * Show the current network state for the first load when the movie list
     * in the adapter is empty and disable swipe to scroll at the first loading
     *
     * @param networkState the new network state
     */
    private void setInitialLoadingState(NetworkState networkState) {
        // Error Message
        mErrorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
        if (networkState.getMessage() != null) {
            mErrorMessageTextView.setText(networkState.getMessage());
        }
        // Loading and Retry
        mRetryButton.setVisibility(networkState.getStatus() == Status.FAILED ? View.VISIBLE : View.GONE);
        mProgressBar.setVisibility(networkState.getStatus() == Status.FAILED ? View.VISIBLE : View.GONE);
        mSwipeRefreshLayout.setEnabled(networkState.getStatus() == Status.SUCCESS);
    }

    @OnClick(R.id.retryLoadingButton)
    void retryInitialLoading() {
        moviesListViewModel.retry();
    }

    @Override
    public void retry() {
        moviesListViewModel.retry();
    }

}
