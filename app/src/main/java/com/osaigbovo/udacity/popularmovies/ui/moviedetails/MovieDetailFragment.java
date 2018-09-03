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

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.di.Injectable;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListActivity;
import com.osaigbovo.udacity.popularmovies.util.ViewsUtils;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import javax.inject.Inject;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

import static com.osaigbovo.udacity.popularmovies.util.AppConstants.BASE_IMAGE_URL;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MoviesListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity} on handsets.
 */
public class MovieDetailFragment extends Fragment implements Injectable {

    public static final String ARG_MOVIE = "movie";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel movieDetailViewModel;

    @BindView(R.id.image_movie_poster)
    ImageView mPosterImage;
    @BindView(R.id.text_movie_title)
    TextView mMovieTitle;
    @BindView(R.id.rating_bar)
    RatingBar mMovieRatingBar;
    @BindView(R.id.text_movie_rating)
    TextView mMovieRating;
    @BindView(R.id.text_movie_release)
    TextView mMovieRelease;
    @BindView(R.id.text_movie_runtime)
    TextView mMovieRuntime;
    @BindView(R.id.text_overview_label)
    TextView mMovieOverviewLabel;
    @BindView(R.id.text_movie_overview)
    TextView mMovieOverview;
    @BindView(R.id.rv_genres)
    RecyclerView mGenreRecyclerView;
    @BindInt(R.integer.detail_desc_slide_duration)
    int slideDuration;

    private GenreAdapter genreAdapter;

    private Unbinder unbinder;

    private Movie movie;

    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = getArguments().getParcelable(ARG_MOVIE);
        }
        movieDetailViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MovieDetailViewModel.class);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_movie_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        genreAdapter = new GenreAdapter();
        FlowLayoutManager mFlowLayoutManager = new FlowLayoutManager();
        mFlowLayoutManager.setAutoMeasureEnabled(true);
        mGenreRecyclerView.setLayoutManager(mFlowLayoutManager);
        mGenreRecyclerView.setAdapter(genreAdapter);

        postponeEnterTransition();

        if (movie != null) {
            // Display Movie Poster
            Context context = mPosterImage.getContext();
            GlideApp.with(context)
                    .load(BASE_IMAGE_URL + movie.getPosterPath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            scheduleStartPostponedEnterTransition(mPosterImage);
                            return false;
                        }

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            scheduleStartPostponedEnterTransition(mPosterImage);
                            return false;
                        }
                    })
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .priority(Priority.HIGH) // Try Priority.IMMEDIATE
                    .centerCrop()
                    .dontTransform()
                    //.placeholder(R.drawable.ic_movie_empty)
                    .onlyRetrieveFromCache(true)
                    .dontAnimate()
                    //.error(R.drawable.ic_movie_error)
                    .into(mPosterImage);

            // Display Movie Original Title
            mMovieTitle.setText(movie.getTitle());
            // Display Movie Rating
            mMovieRating.setText(String.valueOf(movie.getVoteAverage()));
            mMovieRatingBar.setRating(Float.parseFloat(Double.toString(movie.getVoteAverage())) / 2);
            // Display Movie Release Date
            mMovieRelease.setText(ViewsUtils.getDate(movie.getReleaseDate()));
            // Display Movie Synopsis or Overview
            mMovieOverview.setText(movie.getOverview());

            movieDetailViewModel.getMovie(movie.getId());
            movieDetailViewModel.movieDetailMutableLiveData.observe(this, movieDetail -> {
                // Display Movie Genres
                Timber.i(ViewsUtils.getDisplayGenres(movieDetail.getGenres()));
                genreAdapter.addGenres(movieDetail.getGenres());
                // Display Movie Runtime
                mMovieRuntime.setText(ViewsUtils.getDisplayRuntime(movieDetail.getRuntime()));
            });

        } else {
            // TODO : Display some layout to indicate a null movie object.
        }
        return rootView;
    }

    private void scheduleStartPostponedEnterTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ARG_MOVIE, movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            this.movie = savedInstanceState.getParcelable(ARG_MOVIE);
        }
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
