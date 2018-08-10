package com.osaigbovo.udacity.popularmovies.ui.moviedetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListActivity;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.osaigbovo.udacity.popularmovies.data.remote.ApiConstants.BASE_IMAGE_URL;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link MoviesListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    public static final String ARG_MOVIE_ID = "movie_id";

    @BindView(R.id.image_movie_detail)
    ImageView posterImage;
    @BindView(R.id.text_movie_title_detail)
    TextView textMovieTitle;
    @BindInt(R.integer.detail_desc_slide_duration)
    int slideDuration;

    private Unbinder unbinder;

    private TopMovies topMovies;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(MovieDetailFragment.ARG_MOVIE_ID)) {
            topMovies = intent.getExtras().getParcelable(MovieDetailFragment.ARG_MOVIE_ID);
            Timber.i(String.valueOf(topMovies.getTitle()));

            Activity activity = this.getActivity();

            CollapsingToolbarLayout collapsingToolbarLayout = activity.findViewById(R.id.collapsing_toolbar_detail);
            if (collapsingToolbarLayout != null) {
                collapsingToolbarLayout.setTitle(topMovies.getTitle());
            }

            ImageView backdropImage = activity.findViewById(R.id.image_back_drop);
            GlideApp.with(activity)
                    .load(BASE_IMAGE_URL + topMovies.getBackdropPath())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .priority(Priority.HIGH)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(backdropImage);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_movie_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        if (topMovies != null) {

            GlideApp.with(getActivity())
                    .load(BASE_IMAGE_URL + topMovies.getPosterPath())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .priority(Priority.HIGH)
                    .skipMemoryCache(true)
                    .centerCrop()
                    /*.dontAnimate()
                    .placeholder(R.drawable.ic_movie_empty)
                    .error(R.drawable.ic_movie_error)*/
                    /*.listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            postponeEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            postponeEnterTransition();
                            return false;
                        }
                    })*/
                    .into(posterImage);

            textMovieTitle.setText(topMovies.getTitle());
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
