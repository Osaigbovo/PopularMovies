package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.MovieResponse;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
import com.osaigbovo.udacity.popularmovies.data.remote.RequestInterface;
import com.osaigbovo.udacity.popularmovies.data.remote.ServiceGenerator;
import com.osaigbovo.udacity.popularmovies.ui.base.BaseActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.osaigbovo.udacity.popularmovies.data.remote.ApiConstants.API_KEY;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MoviesListActivity extends BaseActivity {

//    @BindString(R.string.title) String title;
//    @BindDrawable(R.drawable.graphic) Drawable graphic;
//    @BindColor(R.color.red) int red; // int or ColorStateList field

    @BindView(R.id.recycler_movies_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindInt(R.integer.movies_columns)
    int mColumns;
    @BindDimen(R.dimen.grid_item_spacing)
    int mGridSpacing;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MovieViewModel movieViewModel;

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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Fade fade = new Fade();
//            fade.excludeTarget(R.id.app_bar, true);
//            fade.excludeTarget(android.R.id.statusBarBackground, true);
//            fade.excludeTarget(android.R.id.navigationBarBackground, true);
//
//            getWindow().setEnterTransition(fade);
//            getWindow().setExitTransition(fade);
//        }

        //assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.movies_columns));

        MoviesListAdapter moviesListAdapter = new MoviesListAdapter(this, mTwoPane);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        movieViewModel.moviesList.observe(this, moviesListAdapter::submitList);

        recyclerView.setAdapter(moviesListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
