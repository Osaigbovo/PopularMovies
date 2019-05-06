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
package com.osaigbovo.udacity.popularmovies.ui.moviedetails;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.util.ColorUtils;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.osaigbovo.udacity.popularmovies.util.AppConstants.BASE_BACKDROP_URL;
import static com.osaigbovo.udacity.popularmovies.util.ColorUtils.WHITE_FILTER;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * movie details are presented side-by-side with a list of movies
 */
public class MovieDetailActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @BindView(R.id.collapsing_toolbar_detail)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.image_movie_backdrop) ImageView mBackdropImage;
    @BindView(R.id.toolbar_detail)
    Toolbar mToolbar;

    @Inject DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject ViewModelProvider.Factory viewModelFactory;

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

        // Show the Up button in the action bar.
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        }

        Intent intent = getIntent();
        if (intent.hasExtra(MovieDetailFragment.ARG_MOVIE)) {
            if (intent.getExtras().getParcelable(MovieDetailFragment.ARG_MOVIE) instanceof Movie) {
                Movie movie = intent.getExtras().getParcelable(MovieDetailFragment.ARG_MOVIE);
                onLoadCollapsingTitle(movie.getTitle());
                onLoadBackDropImage(movie.getBackdropPath());
            } else if (intent.getExtras().getParcelable(MovieDetailFragment.ARG_MOVIE) instanceof MovieDetail) {
                MovieDetail movieDetail = intent.getExtras().getParcelable(MovieDetailFragment.ARG_MOVIE);
                onLoadCollapsingTitle(movieDetail.getTitle());
                onLoadBackDropImage(movieDetail.getBackdropPath());
            }
            // savedInstanceState is non-null when there is fragment state
            // saved from previous configurations of this activity
            // (e.g. when rotating the screen from portrait to landscape).
            // In this case, the fragment will automatically be re-added
            // to its container so we don't need to manually add it.
            if (savedInstanceState == null) {
                // Create the detail fragment and add it to the activity
                // using a fragment transaction.
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.item_detail_container, MovieDetailFragment
                                .newInstance(intent.getExtras().getParcelable(MovieDetailFragment.ARG_MOVIE)))
                        .commit();
            }
        }
    }

    private void onLoadCollapsingTitle(String title) {
        Timber.i(String.valueOf(title));
        mCollapsingToolbarLayout.setTitle(title);
    }

    private void onLoadBackDropImage(String backdropURL) {
        GlideApp.with(this)
                .load(BASE_BACKDROP_URL + backdropURL)
                .listener(backDropImageListener)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.IMMEDIATE)
                .transition(withCrossFade())
                .centerCrop()
                .into(mBackdropImage);
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

    private final RequestListener<Drawable> backDropImageListener = new RequestListener<Drawable>() {
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
                            mCollapsingToolbarLayout.setBackgroundColor(statusBarColor);
                            mCollapsingToolbarLayout.setContentScrimColor(statusBarColor);
                            mCollapsingToolbarLayout.setStatusBarScrimColor(statusBarColor);
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
}
