package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.Status;
import com.osaigbovo.udacity.popularmovies.ui.base.BaseActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.util.RetryCallback;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of movies, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * movie details. On tablets, the activity presents the list of movies and
 * movie details side-by-side using two vertical panes.
 */
public class MoviesListActivity extends BaseActivity implements RetryCallback{

    @BindView(R.id.moviesSwipeRefreshLayout) SwipeRefreshLayout moviesSwipeRefreshLayout;
    @BindView(R.id.recycler_movies_list) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindInt(R.integer.movies_columns) int mColumns;
    @BindDimen(R.dimen.grid_item_spacing) int mGridSpacing;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private MoviesListViewModel moviesListViewModel;
    private MoviesListAdapter moviesListAdapter;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);

        moviesListViewModel = ViewModelProviders.of(this, viewModelFactory).get(MoviesListViewModel.class);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
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
        moviesListViewModel.moviesList.observe(this, moviesListAdapter::submitList);
        recyclerView.setAdapter(moviesListAdapter);
    }

    /**
     * Init swipe to refresh and enable pull to refresh only when there are items in the adapter
     */
    private void initSwipeToRefresh() {
        moviesListViewModel.getRefreshState().observe(this, networkState -> {
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
        moviesSwipeRefreshLayout.setOnRefreshListener(() -> moviesListViewModel.refresh());
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
        moviesListViewModel.retry();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivityFromTransitionManager(this);
    }

    private static void removeActivityFromTransitionManager(Activity activity) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        Class transitionManagerClass = TransitionManager.class;
        try {
            Field runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
            runningTransitionsField.setAccessible(true);
            //noinspection unchecked
            ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> runningTransitions
                    = (ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>)
                    runningTransitionsField.get(transitionManagerClass);
            if (runningTransitions.get() == null || runningTransitions.get().get() == null) {
                return;
            }
            ArrayMap map = runningTransitions.get().get();
            View decorView = activity.getWindow().getDecorView();
            if (map.containsKey(decorView)) {
                map.remove(decorView);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
