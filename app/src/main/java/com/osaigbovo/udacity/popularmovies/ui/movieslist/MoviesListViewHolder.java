package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailFragment;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.osaigbovo.udacity.popularmovies.data.remote.ApiConstants.BASE_IMAGE_URL;
import static com.osaigbovo.udacity.popularmovies.util.ViewsUtils.getYearOfRelease;

public class MoviesListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_movie)
    ImageView movieImage;
    @BindView(R.id.text_movie_title)
    TextView movieTitle;
    @BindView(R.id.text_movie_date)
    TextView dateText;
    @BindView(R.id.text_movie_rating)
    TextView ratingText;

    MoviesListViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindTo(TopMovies topMovies, MoviesListActivity mParentActivity, boolean mTwoPane) {
        Timber.i(topMovies.getOriginalTitle());

        String image_url = BASE_IMAGE_URL + topMovies.getPosterPath();
        GlideApp.with(itemView.getContext())
                .load(image_url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.HIGH)
                .skipMemoryCache(true)
                .fitCenter()
                .transition(withCrossFade())
                /*.placeholder(R.drawable.ic_movie_empty)
                .error(R.drawable.ic_movie_error)*/
                .into(movieImage);

        movieTitle.setText(topMovies.getTitle());
        dateText.setText(getYearOfRelease(topMovies.getReleaseDate()));
        ratingText.setText(String.valueOf(topMovies.getVoteAverage()));

        itemView.setTag(topMovies);
        itemView.setOnClickListener(view -> {
            TopMovies mTopMovies = (TopMovies) view.getTag();
            Timber.i(String.valueOf(topMovies));
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putParcelable(MovieDetailFragment.ARG_MOVIE, topMovies);
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.ARG_MOVIE, mTopMovies);

                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((Activity) context, movieImage, context.getResources()
                                .getString(R.string.transition_name));
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    public static MoviesListViewHolder create(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_movie, parent, false);
        return new MoviesListViewHolder(view);
    }
}
