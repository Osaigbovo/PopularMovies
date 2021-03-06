package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.osaigbovo.udacity.popularmovies.PopularMoviesApp;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.ui.base.BaseActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.adapters.FavoriteMoviesAdapter;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.adapters.MoviesListAdapter;
import com.osaigbovo.udacity.popularmovies.ui.search.SearchActivity;
import com.osaigbovo.udacity.popularmovies.ui.widget.CustomItemAnimator;
import com.osaigbovo.udacity.popularmovies.ui.widget.ItemOffsetDecoration;
import com.osaigbovo.udacity.popularmovies.util.AppConstants;
import com.osaigbovo.udacity.popularmovies.util.RetryCallback;
import com.osaigbovo.udacity.popularmovies.util.SharedPreferenceUtils;
import com.osaigbovo.udacity.popularmovies.util.ViewsUtils;

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
 *
 * @author Osaigbovo Odiase.
 */
public class MoviesListActivity extends BaseActivity implements RetryCallback {

    private static final int RC_SEARCH = 0;
    private static final String SAVED_LAYOUT_MANAGER = "layout_manager_state";
    private static final String BUNDLE_RECYCLER_POSITION_KEY = "recycler_position";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MoviesListViewModel moviesListViewModel;

    @BindView(R.id.moviesSwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_movies_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.recycler_favorites_list)
    RecyclerView mFavoriteRecyclerView;
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
    @BindView(R.id.item_favorite_state)
    View noFavorites;
    @BindDimen(R.dimen.grid_item_spacing)
    int mGridSpacing;

    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;

    private MoviesListAdapter moviesListAdapter;
    private FavoriteMoviesAdapter favoriteMoviesAdapter;
    private Parcelable layoutManagerSavedState;

    private Paint p = new Paint();

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
        setupRecyclerView();
        initSwipeToRefresh();

        if (savedInstanceState == null) {
            loadMoviesFromSharedPrefs();
        }
    }

    private void setupRecyclerView() {
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.default_spacing_small);

        gridLayoutManager = new GridLayoutManager(this, mColumns);
        linearLayoutManager = new LinearLayoutManager(this);

        moviesListAdapter = new MoviesListAdapter(this, this, mTwoPane);
        favoriteMoviesAdapter = new FavoriteMoviesAdapter(this, mTwoPane);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mFavoriteRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        CustomItemAnimator animator = new CustomItemAnimator(newHolder ->
                favoriteMoviesAdapter.removeItem(newHolder.getAdapterPosition()));
        mFavoriteRecyclerView.setItemAnimator(animator);
        mFavoriteRecyclerView.addItemDecoration(new ItemOffsetDecoration(spacing));
        mRecyclerView.setAdapter(moviesListAdapter);
        mFavoriteRecyclerView.setAdapter(favoriteMoviesAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVED_LAYOUT_MANAGER, gridLayoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        layoutManagerSavedState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (layoutManagerSavedState != null) {
            gridLayoutManager.onRestoreInstanceState(layoutManagerSavedState);
            setupList();
        }

        if (mRecyclerView != null) {
            postponeEnterTransition();
            mRecyclerView.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
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
    protected void onStart() {
        super.onStart();
        //noFavorites.setVisibility(mRecyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                sortMovies();
                return true;
            case R.id.menu_search:
                View searchMenuView = mToolbar.findViewById(R.id.menu_search);
                Bundle options = ActivityOptions
                        .makeSceneTransitionAnimation(this, searchMenuView,
                                getString(R.string.transition_search_back)).toBundle();

                ActivityCompat.startActivityForResult(this,
                        new Intent(this, SearchActivity.class),
                        RC_SEARCH,
                        options);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SEARCH:
                // reset the search icon which we hid
                View searchMenuView = mToolbar.findViewById(R.id.menu_search);
                if (searchMenuView != null) {
                    searchMenuView.setAlpha(1f);
                }
                if (resultCode == SearchActivity.RESULT_CODE_SAVE) {
                    String query = data.getStringExtra(SearchActivity.EXTRA_QUERY);
                    if (TextUtils.isEmpty(query)) return;
                }
                break;
        }
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
        noFavorites.setVisibility(View.GONE);
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
                } else if (SharedPreferenceUtils
                        .getSharedPreferenceString(AppConstants.PREF_FILTER, null)
                        .equals(AppConstants.SORT_BY_FAVORITE)) {
                    loadMovies(AppConstants.SORT_BY_FAVORITE);
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
        RadioButton mRadioButtonFavorite = view.findViewById(R.id.radio_button_favorite);

        switch (SharedPreferenceUtils.getSharedPreferenceString(AppConstants.PREF_FILTER, null)) {
            case AppConstants.SORT_BY_TOP_RATED:
                mRadioButtonTopRated.setChecked(true);
                break;
            case AppConstants.SORT_BY_POPULAR:
                mRadioButtonPopular.setChecked(true);
                break;
            case AppConstants.SORT_BY_FAVORITE:
                mRadioButtonFavorite.setChecked(true);
                break;
        }

        mRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            mBottomSheetDialog.dismiss();
            switch (checkedId) {
                case R.id.radio_button_popular:
                    noFavorites.setVisibility(View.GONE);
                    if (PopularMoviesApp.hasNetwork()) {
                        SharedPreferenceUtils.setSharedPreferenceString(AppConstants.PREF_FILTER, AppConstants.SORT_BY_POPULAR);
                        Timber.i("Get Popular Movies");
                        loadMovies(AppConstants.SORT_BY_POPULAR);
                    } else {
                        showMessage(PopularMoviesApp.hasNetwork());
                    }
                    break;
                case R.id.radio_button_top_rated:
                    noFavorites.setVisibility(View.GONE);
                    if (PopularMoviesApp.hasNetwork()) {
                        SharedPreferenceUtils.setSharedPreferenceString(AppConstants.PREF_FILTER, AppConstants.SORT_BY_TOP_RATED);
                        Timber.i("Get Top Rated Movies");
                        loadMovies(AppConstants.SORT_BY_TOP_RATED);
                    } else {
                        showMessage(PopularMoviesApp.hasNetwork());
                    }
                    break;
                case R.id.radio_button_favorite:
                    SharedPreferenceUtils.setSharedPreferenceString(AppConstants.PREF_FILTER, AppConstants.SORT_BY_FAVORITE);
                    loadMovies(AppConstants.SORT_BY_FAVORITE);
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
        if (sort.equalsIgnoreCase(AppConstants.SORT_BY_FAVORITE)) {
            mSwipeRefreshLayout.setEnabled(false);
            mRecyclerView.setVisibility(View.GONE);
            mFavoriteRecyclerView.setVisibility(View.VISIBLE);
            moviesListViewModel.getFavorites().observe(this, favoriteMovies -> {
                if (favoriteMovies != null && favoriteMovies.size() != 0) {
                    favoriteMoviesAdapter.addMoviesList(favoriteMovies);
                } else {
                    mFavoriteRecyclerView.setVisibility(View.GONE);
                    noFavorites.setVisibility(View.VISIBLE);
                }
            });

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(mFavoriteRecyclerView);
        } else {
            mFavoriteRecyclerView.setVisibility(View.GONE);
            noFavorites.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            moviesListViewModel.sort(sort);
            setupList();
        }
    }

    // Fetch and observe List. Also submit paged list to Adapter.
    public void setupList() {
        moviesListViewModel.moviesList.observe(this, moviesListAdapter::submitList);
        moviesListViewModel.getNetworkState().observe(this, moviesListAdapter::setNetworkState);
    }

    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
            .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder instanceof FavoriteMoviesAdapter.ViewHolder) {

                final int position = viewHolder.getAdapterPosition();
                final MovieDetail movieDetail = favoriteMoviesAdapter.removeItem(position);

                moviesListViewModel.removeFavorite(movieDetail);
                favoriteMoviesAdapter.notifyItemRemoved(position);
                favoriteMoviesAdapter.notifyDataSetChanged();
                //favoriteMoviesAdapter.notifyItemRangeChanged(position, favMoviesList.size());

                // Show SnackBar with Movie name and Undo option
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator),
                        movieDetail.getOriginalTitle() + " is removed from Favorites!",
                        Snackbar.LENGTH_LONG);

                snackbar.setAction("UNDO", view -> {
                    // Undo is selected, restore the deleted item
                    moviesListViewModel.addFavorite(movieDetail);
                    favoriteMoviesAdapter.restoreItem(movieDetail, position);
                    if (favoriteMoviesAdapter.getItemCount() != 0) {
                        mFavoriteRecyclerView.setVisibility(View.VISIBLE);
                        noFavorites.setVisibility(View.GONE);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;
                p.setAntiAlias(true);
                p.setColor(Color.parseColor("#388E3C"));

                if (dX > 0) {
                    RectF background = new RectF((float) itemView.getLeft(),
                            (float) itemView.getTop(), dX, (float) itemView.getBottom());
                    RectF iconDest = new RectF((float) itemView.getLeft() + width,
                            (float) itemView.getTop() + width,
                            (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                    c.drawRect(background, p);
                    c.drawBitmap(ViewsUtils.getBitmap(getDrawable(R.drawable.ic_delete)), null, iconDest, p);
                } else {
                    RectF background = new RectF((float) itemView.getRight() + dX,
                            (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    RectF iconDest = new RectF((float) itemView.getRight() - 2 * width,
                            (float) itemView.getTop() + width, (float) itemView.getRight() - width,
                            (float) itemView.getBottom() - width);
                    c.drawRect(background, p);
                    c.drawBitmap(ViewsUtils.getBitmap(getDrawable(R.drawable.ic_delete)), null, iconDest, p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @OnClick(R.id.retryLoadingButton)
    void retryInitialLoading() {
        moviesListViewModel.retry();
    }

    @Override
    public void retry() {
        moviesListViewModel.retry();
    }
}
