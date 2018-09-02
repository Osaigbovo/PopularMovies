package com.osaigbovo.udacity.popularmovies.ui.moviedetails;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.osaigbovo.udacity.popularmovies.data.model.Genre;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
import com.osaigbovo.udacity.popularmovies.di.Injectable;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListActivity;
import com.osaigbovo.udacity.popularmovies.util.ViewsUtils;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

import static com.osaigbovo.udacity.popularmovies.data.remote.ApiConstants.BASE_IMAGE_URL;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MoviesListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity} on handsets.
 */
public class MovieDetailFragment extends Fragment implements Injectable {

    public static final String ARG_MOVIE = "movie_id";

    @BindView(R.id.image_movie_poster)
    ImageView posterImage;
    @BindView(R.id.text_movie_title)
    TextView mMovieTitle;
    @BindView(R.id.rating_bar)
    RatingBar mMovieRatingBar;
    @BindView(R.id.text_movie_rating)
    TextView mMovieRating;
    @BindView(R.id.text_release_label)
    TextView mMovieReleaseLabel;
    @BindView(R.id.text_movie_release)
    TextView mMovieRelease;
    @BindView(R.id.text_runtime_label)
    TextView mMovieRuntimeLabel;
    @BindView(R.id.text_movie_runtime)
    TextView mMovieRuntime;
    @BindView(R.id.text_overview_label)
    TextView mMovieOverviewLabel;
    @BindView(R.id.text_movie_overview)
    TextView mMovieOverview;
    @BindInt(R.integer.detail_desc_slide_duration)
    int slideDuration;

    @BindView(R.id.rv_genres)
    RecyclerView mRvGenres;
    private GenreAdapter genreAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel movieDetailViewModel;
    private Unbinder unbinder;

    private TopMovies topMovies;
    //private OnFragmentInteractionListener mListener;

    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(TopMovies topMovies) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, topMovies);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topMovies = getArguments().getParcelable(ARG_MOVIE);
        }
        movieDetailViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailViewModel.class);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_movie_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        genreAdapter = new GenreAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRvGenres.setLayoutManager(llm);
        mRvGenres.setAdapter(genreAdapter);

        postponeEnterTransition();

        if (topMovies != null) {
            // Display Movie Poster
            Context context = posterImage.getContext();
            GlideApp.with(context)
                    .load(BASE_IMAGE_URL + topMovies.getPosterPath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            scheduleStartPostponedEnterTransition(posterImage);
                            return false;
                        }

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            scheduleStartPostponedEnterTransition(posterImage);
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
                    .into(posterImage);

            // Display Movie Original Title
            mMovieTitle.setText(topMovies.getOriginalTitle());
            // Display Movie Rating
            mMovieRating.setText(String.valueOf(topMovies.getVoteAverage()));
            mMovieRatingBar.setRating(Float.parseFloat(Double.toString(topMovies.getVoteAverage())) / 2);
            // Display Movie Release Date
            mMovieRelease.setText(ViewsUtils.getDate(topMovies.getReleaseDate()));
            // Display Movie Synopsis or Overview
            if (!TextUtils.isEmpty(topMovies.getOverview())) {
                mMovieOverview.setText(topMovies.getOverview());
            } else {
                mMovieOverview.setVisibility(View.GONE);
                mMovieOverviewLabel.setVisibility(View.GONE);
            }

            movieDetailViewModel.getMovie(topMovies.getId());
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
        outState.putParcelable(ARG_MOVIE, topMovies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            this.topMovies = savedInstanceState.getParcelable(ARG_MOVIE);
        }
    }

    private void getGenres(List<Genre> genresID) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /*public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }*/
}
