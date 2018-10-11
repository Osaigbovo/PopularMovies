package com.osaigbovo.udacity.popularmovies.ui.moviedetails;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.Cast;
import com.osaigbovo.udacity.popularmovies.data.model.Credits;
import com.osaigbovo.udacity.popularmovies.data.model.Crew;
import com.osaigbovo.udacity.popularmovies.data.model.Genre;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.data.model.Video;
import com.osaigbovo.udacity.popularmovies.data.model.Videos;
import com.osaigbovo.udacity.popularmovies.di.Injectable;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.adapters.CastAdapter;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.adapters.GenreAdapter;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.adapters.ReviewAdapter;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.adapters.VideoAdapter;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListActivity;
import com.osaigbovo.udacity.popularmovies.ui.widget.CircularImageView;
import com.osaigbovo.udacity.popularmovies.ui.widget.WishListIconView;
import com.osaigbovo.udacity.popularmovies.util.ViewsUtils;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

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
 *
 * @author Osaigbovo Odiase.
 */
public class MovieDetailFragment extends Fragment implements Injectable {

    public static final String ARG_MOVIE = "movie";
    public static final String FAVORITE_STATE = "clicked";

    @Inject ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel movieDetailViewModel;

    @BindView(R.id.favoriteIcon) WishListIconView mFavoriteLottie;
    @BindView(R.id.image_movie_poster) ImageView mPosterImage;
    @BindView(R.id.text_movie_title) TextView mMovieTitle;
    @BindView(R.id.rating_bar) RatingBar mMovieRatingBar;
    @BindView(R.id.text_movie_rating) TextView mMovieRating;
    @BindView(R.id.text_movie_release) TextView mMovieRelease;
    @BindView(R.id.text_movie_runtime) TextView mMovieRuntime;
    @BindView(R.id.text_overview_label) TextView mMovieOverviewLabel;
    @BindView(R.id.text_movie_overview) TextView mMovieOverview;
    @BindView(R.id.image_director) CircularImageView mMovieDirectorImage;
    @BindView(R.id.text_director_name) TextView mMovieDirector;
    @BindView(R.id.text_no_reviews) TextView mMovieNoReviews;
    @BindView(R.id.text_no_videos) TextView mMovieNoVideos;
    @BindView(R.id.recycler_view_genres) RecyclerView mGenreRecyclerView;
    @BindView(R.id.recycler_view_cast) RecyclerView mCastRecyclerView;
    @BindView(R.id.text_video_count) TextView mTrailerCount;
    @BindView(R.id.recycler_view_videos) RecyclerView mVideoRecyclerView;
    @BindView(R.id.progress_bar_video) ProgressBar mVideosProgressBar;
    @BindView(R.id.progress_bar_review) ProgressBar mReviewsProgressBar;
    @BindView(R.id.recycler_view_reviews) RecyclerView mReviewsRecyclerView;

    private GenreAdapter genreAdapter;
    private CastAdapter castAdapter;
    private VideoAdapter videoAdapter;
    private ReviewAdapter reviewAdapter;

    LinearLayoutManager castLayoutManager;
    LinearLayoutManager videoLayoutManager;
    LinearLayoutManager reviewLayoutManager;

    private Unbinder unbinder;

    private Movie movie;
    private MovieDetail movieDetail;
    private MediaPlayer mediaPlayer;

    public MovieDetailFragment() {}

    public static MovieDetailFragment newInstance(Parcelable movie) {
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
            if(getArguments().getParcelable(ARG_MOVIE) instanceof Movie){
                this.movie = getArguments().getParcelable(ARG_MOVIE);
            }else if (getArguments().getParcelable(ARG_MOVIE) instanceof MovieDetail) {
                this.movieDetail = getArguments().getParcelable(ARG_MOVIE);
            }
        }

        movieDetailViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MovieDetailViewModel.class);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_movie_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        postponeEnterTransition();

        mediaPlayer = MediaPlayer.create(this.getActivity(), R.raw.favorite_sound);

        setUpRecyclers();
        checkYoutubeApp(); //Check whether the main Youtube app exists on the device.

        if (movie != null){
            onLoadImage(movie.getPosterPath());
            onLoadTitle(movie.getTitle());
            onLoadRating(movie.getVoteAverage());
            onLoadDate(ViewsUtils.getDate(movie.getReleaseDate()));
            onLoadSynopsis(movie.getOverview());

            movieDetailViewModel.getMovieDetails(movie.getId());
            movieDetailViewModel.movieDetailMutableLiveData.observe(this, movieDetail -> {
                this.movieDetail = movieDetail;
                onLoadGenres(movieDetail.getGenres());
                onLoadRuntime(movieDetail.getRuntime());
                onLoadDirector(movieDetail.getCredits());
                onLoadCast(movieDetail.getCredits());
                onLoadVideo(movieDetail.getVideos());

                movieDetailViewModel.isFavorite(movieDetail.getId()).observe(this, favMovieDetail1 -> {
                    mFavoriteLottie.setActivated(favMovieDetail1 != null);
        /*if(movieDetail!=null){
            mFavoriteLottie.setProgress(1.0F);
            //mFavoriteLottie.setActivated(true);
        }else {
            mFavoriteLottie.setProgress(0.0F);
            //mFavoriteLottie.setActivated(false);
        }*/
                    mFavoriteLottie.setVisibility(View.VISIBLE);
                });
            });

            movieDetailViewModel.getReviews(movie.getId());
            onLoadReview();

        }else {
            onLoadImage(movieDetail.getPosterPath());
            onLoadTitle(movieDetail.getTitle());
            onLoadRating(movieDetail.getVoteAverage());
            onLoadDate(ViewsUtils.getDate(movieDetail.getReleaseDate()));
            onLoadSynopsis(movieDetail.getOverview());
            onLoadGenres(movieDetail.getGenres());
            onLoadRuntime(movieDetail.getRuntime());
            onLoadDirector(movieDetail.getCredits());
            onLoadCast(movieDetail.getCredits());
            onLoadVideo(movieDetail.getVideos());
            movieDetailViewModel.getReviews(movieDetail.getId());
            onLoadReview();

            mFavoriteLottie.setActivated(movieDetail != null);
            mFavoriteLottie.setVisibility(View.VISIBLE);
        }

        mFavoriteLottie.setOnClickListener(view -> {
            mFavoriteLottie.toggleWishlisted();
            onFavoriteClick(movieDetail);
        });

        return rootView;
    }

    private void setUpRecyclers(){
        FlowLayoutManager mFlowLayoutManager = new FlowLayoutManager();
        mFlowLayoutManager.setAutoMeasureEnabled(true);
        mGenreRecyclerView.setLayoutManager(mFlowLayoutManager);
        genreAdapter = new GenreAdapter();
        mGenreRecyclerView.setAdapter(genreAdapter);

        videoLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        mVideoRecyclerView.setLayoutManager(videoLayoutManager);
        videoAdapter = new VideoAdapter();
        mVideoRecyclerView.setAdapter(videoAdapter);

        castLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        mCastRecyclerView.setLayoutManager(castLayoutManager);
        castAdapter = new CastAdapter();
        mCastRecyclerView.setAdapter(castAdapter);

        reviewLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mReviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewAdapter = new ReviewAdapter();
        mReviewsRecyclerView.setAdapter(reviewAdapter);
    }

    private void checkYoutubeApp(){
        final YouTubeInitializationResult result = YouTubeApiServiceUtil
                .isYouTubeApiServiceAvailable(getActivity());
        if (result != YouTubeInitializationResult.SUCCESS) {
            // If there are any issues we can show an error dialog.
            result.getErrorDialog(Objects.requireNonNull(getActivity()), 0).show();
        }
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
        if (mFavoriteLottie.isActivated()) {
            Toast.makeText(getContext(), String.valueOf("Added: " + movieDetail.getTitle()),
                    Toast.LENGTH_SHORT).show();
            movieDetailViewModel.addFavorite(movieDetail);
        } else {
            Toast.makeText(getContext(), String.valueOf("Removed: " + movieDetail.getTitle()),
                    Toast.LENGTH_SHORT).show();
            movieDetailViewModel.removeFavorite(movieDetail);
        }
        mediaPlayer.start();
    }

    // Display Movie Poster
    private void onLoadImage(String imageURL){
        Context context = mPosterImage.getContext();
        GlideApp.with(context)
                .load(BASE_IMAGE_URL + imageURL)
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
    }

    // Display Movie Title
    private void onLoadTitle(String title){
        mMovieTitle.setText(title);
    }

    // Display Movie Rating
    private void onLoadRating(Double rating){
        mMovieRating.setText(String.valueOf(rating));
        mMovieRatingBar.setRating(Float.parseFloat(Double.toString(rating)) / 2);
    }

    // Display Movie Genres
    private void onLoadGenres(ArrayList<Genre> genres){
        Timber.i(ViewsUtils.getDisplayGenres(genres));
        genreAdapter.addGenres(genres);
    }

    // Display Movie Runtime
    private void onLoadRuntime(int runtime){
        mMovieRuntime.setText(ViewsUtils.getDisplayRuntime(runtime));
    }

    // Display Movie Release Date
    private void onLoadDate(String date){
        mMovieRelease.setText(date);
    }

    // Display Movie Synopsis or Overview
    private void onLoadSynopsis(String synopsis){
        mMovieOverview.setText(synopsis);
    }

    // Display Movie Director
    private void onLoadDirector(@NonNull Credits credits) {
        Context context = mMovieDirectorImage.getContext();
        Crew crew = ViewsUtils.getDisplayDirector(credits.getCrew());

        if (crew != null) {
            mMovieDirector.setText(crew.getName());
            GlideApp.with(context)
                    .load(BASE_IMAGE_URL + crew.getProfilePath())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .priority(Priority.HIGH)
                    .placeholder(R.drawable.ic_crew_cast)
                    .into(mMovieDirectorImage);
        } else {
            mMovieDirector.setVisibility(View.GONE);
        }
    }

    // Display Movie Casts
    private void onLoadCast(Credits credits) {
        ArrayList<Cast> castList = credits.getCast();
        castAdapter.addCasts(castList);
    }

    // Display Movie Trailers
    private void onLoadVideo(Videos videos) {
        ArrayList<Video> videoList = videos.getVideos();
        mVideosProgressBar.setVisibility(View.GONE);
        if (videoList.size() > 0) {
            videoAdapter.setVideo(videoList);
            mTrailerCount.setText(String.valueOf(videoAdapter.getItemCount()));
            mVideoRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mTrailerCount.setVisibility(View.INVISIBLE);
            mMovieNoVideos.setVisibility(View.VISIBLE);
        }
    }

    // Display Movie Reviews
    private void onLoadReview() {
        movieDetailViewModel.reviewsMutableLiveData.observe(this, reviews -> {
            mReviewsProgressBar.setVisibility(View.GONE);
            if (Objects.requireNonNull(reviews).getReviews() != null && reviews.getReviews().size() > 0) {
                reviewAdapter.addReviews(reviews.getReviews());
                mReviewsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mMovieNoReviews.setVisibility(View.VISIBLE);
            }
        });
    }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            sharingTrailer(movieDetail.getVideos().getVideos().get(0));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.release();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
        unbinder.unbind();
    }

    // Share the first trailer
    private void sharingTrailer(Video video) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, movieDetail.getTitle() + " - " + video.getName());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + video.getKey());
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_trailer)));
    }
}
