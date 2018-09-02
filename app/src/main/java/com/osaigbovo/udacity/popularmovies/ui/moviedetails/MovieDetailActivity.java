package com.osaigbovo.udacity.popularmovies.ui.moviedetails;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
import com.osaigbovo.udacity.popularmovies.util.ColorUtils;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.osaigbovo.udacity.popularmovies.data.remote.ApiConstants.BASE_BACKDROP_URL;
import static com.osaigbovo.udacity.popularmovies.util.ColorUtils.WHITE_FILTER;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 */
public class MovieDetailActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private TopMovies topMovies;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView backdropImage;

    @BindView(R.id.toolbar_detail)
    Toolbar toolbar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel movieDetailViewModel;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        //movieDetailViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailViewModel.class);

        // Show the Up button in the action bar.
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }

        Intent intent = getIntent();
        if (intent.hasExtra(MovieDetailFragment.ARG_MOVIE)) {
            topMovies = intent.getExtras().getParcelable(MovieDetailFragment.ARG_MOVIE);
            Timber.i(String.valueOf(topMovies.getTitle()));

            collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_detail);
            if (collapsingToolbarLayout != null) {
                collapsingToolbarLayout.setTitle(topMovies.getTitle());
            }

            backdropImage = findViewById(R.id.image_movie_backdrop);
            GlideApp.with(this)
                    .load(BASE_BACKDROP_URL + topMovies.getBackdropPath())
                    .listener(backDropImageListener)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .priority(Priority.IMMEDIATE)
                    .transition(withCrossFade())
                    .centerCrop()
                    .into(backdropImage);
        }
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, MovieDetailFragment.newInstance(topMovies))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private RequestListener<Drawable> backDropImageListener = new RequestListener<Drawable>() {
        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                       DataSource dataSource, boolean isFirstResource) {
            final Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
            if (bitmap == null) return false;

            Palette.from(bitmap)
                    .addFilter(WHITE_FILTER)
                    .generate(palette -> {
                        int statusBarColor = getWindow().getStatusBarColor();
                        final Palette.Swatch topColor = ColorUtils.getMostPopulousSwatch(palette);

                        if (topColor != null) {
                            statusBarColor = topColor.getRgb();
                            topColor.getTitleTextColor();
                        }
                        if (statusBarColor != getWindow().getStatusBarColor()) {
                            collapsingToolbarLayout.setBackgroundColor(statusBarColor);
                            collapsingToolbarLayout.setContentScrimColor(statusBarColor);
                            collapsingToolbarLayout.setStatusBarScrimColor(statusBarColor);
                        }
                    });
            return false;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                    Target<Drawable> target, boolean isFirstResource) {
            return false;
        }
    };

    private static void removeActivityFromTransitionManager(Activity activity) {
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
