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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.osaigbovo.udacity.popularmovies.PopularMoviesApp;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.data.model.Video;
import com.osaigbovo.udacity.popularmovies.data.model.Videos;
import com.osaigbovo.udacity.popularmovies.di.Injectable;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListActivity;
import com.osaigbovo.udacity.popularmovies.ui.widget.WishListIconView;
import com.osaigbovo.udacity.popularmovies.util.ViewsUtils;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;

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
    public static final String FAVORITE_STATE = "clicked";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel movieDetailViewModel;

    /*@BindView(R.id.favoriteIcon)
    LottieAnimationView mLottie;*/
    @BindView(R.id.favoriteIcon)
    WishListIconView mFavoriteLottie;
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
    @BindView(R.id.recycler_view_genres)
    RecyclerView mGenreRecyclerView;
    @BindView(R.id.recycler_view_videos)
    RecyclerView mVideoRecyclerView;
    @BindInt(R.integer.detail_desc_slide_duration)
    int slideDuration;

    LinearLayoutManager videoLayoutManager;

    private GenreAdapter genreAdapter;
    private VideoAdapter videoAdapter;

    private Unbinder unbinder;

    private Movie movie;
    private MovieDetail movieDetail;
    private MediaPlayer mediaPlayer;

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

        /*if (savedInstanceState != null){
            mFavoriteLottie.setActivated(savedInstanceState.getBoolean(FAVORITE_STATE));
        }*/
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_movie_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        //ImeUtils.hideKeyboard(this.getActivity());

        mFavoriteLottie.setVisibility(View.INVISIBLE);

        mediaPlayer = MediaPlayer.create(this.getActivity(), R.raw.favorite_sound);

        genreAdapter = new GenreAdapter();
        FlowLayoutManager mFlowLayoutManager = new FlowLayoutManager();
        mFlowLayoutManager.setAutoMeasureEnabled(true);
        mGenreRecyclerView.setLayoutManager(mFlowLayoutManager);
        mGenreRecyclerView.setAdapter(genreAdapter);


        videoAdapter = new VideoAdapter();
        videoLayoutManager = new LinearLayoutManager(PopularMoviesApp.getContext(), LinearLayoutManager.HORIZONTAL,
                false);
        mVideoRecyclerView.setLayoutManager(videoLayoutManager);
        //mVideoRecyclerView.setHasFixedSize(true);

        //Check whether the main Youtube app exists on the device.
        final YouTubeInitializationResult result = YouTubeApiServiceUtil
                .isYouTubeApiServiceAvailable(getActivity());
        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(getActivity(), 0).show();
        }

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


            movieDetailViewModel.getMovieDetails(movie.getId());
            movieDetailViewModel.movieDetailMutableLiveData.observe(this, movieDetail -> {
                this.movieDetail = movieDetail;

                // Display Movie Genres
                Timber.i(ViewsUtils.getDisplayGenres(movieDetail.getGenres()));
                genreAdapter.addGenres(movieDetail.getGenres());
                // Display Movie Runtime
                mMovieRuntime.setText(ViewsUtils.getDisplayRuntime(movieDetail.getRuntime()));


                onLoadVideo(movieDetail.getVideos());
            });

            movieDetailViewModel.isFavorite(movie.getId()).observe(this, movieDetail -> {

                mFavoriteLottie.setActivated(movieDetail != null);
                mFavoriteLottie.setZ(4.0F);

                /*if(movieDetail!=null){
                    mFavoriteLottie.setProgress(1.0F);
                    //mFavoriteLottie.setActivated(true);
                }else {
                    mFavoriteLottie.setProgress(0.0F);
                    //mFavoriteLottie.setActivated(false);
                }*/
                mFavoriteLottie.setVisibility(View.VISIBLE);

            });

            mFavoriteLottie.setOnClickListener(view -> {
                mFavoriteLottie.toggleWishlisted();
                onFavoriteClick(movieDetail);
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

    public void onFavoriteClick(MovieDetail movieDetail) {
        mediaPlayer.start();
        if(mFavoriteLottie.isActivated()){
            Toast.makeText(getContext(), String.valueOf("Added: " + movieDetail.getOriginalTitle()),
                    Toast.LENGTH_SHORT).show();
            movieDetailViewModel.addFavorite(movieDetail);
        }else {
            Toast.makeText(getContext(), String.valueOf("Removed: " + movieDetail.getOriginalTitle()),
                    Toast.LENGTH_SHORT).show();
            movieDetailViewModel.removeFavorite(movieDetail);
        }
    }

    private void onLoadVideo(Videos videos) {
        ArrayList<Video> videoList = videos.getVideos();
        videoAdapter.setVideo(videoList);
        mVideoRecyclerView.setAdapter(videoAdapter);
    }

    // TODO - save sate of Favorite Icon
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(FAVORITE_STATE, mFavoriteLottie.isActivated());

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
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
            }
        });
        unbinder.unbind();
    }
}
