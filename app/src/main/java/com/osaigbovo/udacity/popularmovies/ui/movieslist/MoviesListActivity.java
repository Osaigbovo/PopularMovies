package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.Status;
import com.osaigbovo.udacity.popularmovies.ui.base.BaseActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.util.RetryCallback;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MoviesListActivity extends BaseActivity implements RetryCallback {

//    @BindString(R.string.title) String title;
//    @BindDrawable(R.drawable.graphic) Drawable graphic;
//    @BindColor(R.color.red) int red; // int or ColorStateList field

    @BindView(R.id.moviesSwipeRefreshLayout)
    SwipeRefreshLayout moviesSwipeRefreshLayout;

    @BindView(R.id.recycler_movies_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindInt(R.integer.movies_columns)
    int mColumns;

    @BindDimen(R.dimen.grid_item_spacing)
    int mGridSpacing;

    private MovieViewModel movieViewModel;
    private MoviesListAdapter moviesListAdapter;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);
        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(R.id.app_bar, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }

        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);
        initSwipeToRefresh();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.movies_columns));

        moviesListAdapter = new MoviesListAdapter(this, mTwoPane);
        recyclerView.setLayoutManager(gridLayoutManager);
        movieViewModel.moviesList.observe(this, moviesListAdapter::submitList);
        recyclerView.setAdapter(moviesListAdapter);
    }

    /**
     * Init swipe to refresh and enable pull to refresh only when there are items in the adapter
     */
    private void initSwipeToRefresh() {
        movieViewModel.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {
                if (moviesListAdapter.getCurrentList() != null) {
                    if (moviesListAdapter.getCurrentList().size() > 0) {
                        moviesSwipeRefreshLayout.setRefreshing(
                                networkState.getStatus() == NetworkState.LOADING.getStatus());
                    } else {
                        setInitialLoadingState(networkState);
                    }
                } else {
                    setInitialLoadingState(networkState);
                }
            }
        });
        moviesSwipeRefreshLayout.setOnRefreshListener(() -> movieViewModel.refresh());
    }

    /**
     * Show the current network state for the first load when the user list
     * in the adapter is empty and disable swipe to scroll at the first loading
     *
     * @param networkState the new network state
     */
    private void setInitialLoadingState(NetworkState networkState) {
        //error message
//        errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
//        if (networkState.getMessage() != null) {
//            errorMessageTextView.setText(networkState.getMessage());
//        }
//
//        //loading and retry
//        retryLoadingButton.setVisibility(networkState.getStatus() == Status.FAILED ? View.VISIBLE : View.GONE);
//        loadingProgressBar.setVisibility(networkState.getStatus() == Status.RUNNING ? View.VISIBLE : View.GONE);

        moviesSwipeRefreshLayout.setEnabled(networkState.getStatus() == Status.SUCCESS);
    }

    @Override
    public void retry() {
        movieViewModel.retry();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
