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
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailFragment;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.osaigbovo.udacity.popularmovies.util.AppConstants.BASE_IMAGE_URL;
import static com.osaigbovo.udacity.popularmovies.util.ViewsUtils.getYearOfRelease;

public class MoviesListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_movie)
    ImageView mMovieImage;
    @BindView(R.id.text_movie_title)
    TextView mMovieTitle;
    @BindView(R.id.text_movie_date)
    TextView mMovieDate;
    @BindView(R.id.text_movie_rating)
    TextView mMovieRating;

    MoviesListViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindTo(Movie movie, MoviesListActivity mParentActivity, boolean mTwoPane) {
        Timber.i(movie.getOriginalTitle());

        String image_url = BASE_IMAGE_URL + movie.getPosterPath();
        GlideApp.with(itemView.getContext())
                .load(image_url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.HIGH)
                .skipMemoryCache(true)
                .fitCenter()
                .transition(withCrossFade())
                /*.placeholder(R.drawable.ic_movie_empty)
                .error(R.drawable.ic_movie_error)*/
                .into(mMovieImage);

        mMovieTitle.setText(movie.getTitle());
        mMovieDate.setText(getYearOfRelease(movie.getReleaseDate()));
        mMovieRating.setText(String.valueOf(movie.getVoteAverage()));

        itemView.setTag(movie);
        itemView.setOnClickListener(view -> {
            Movie mMovie = (Movie) view.getTag();
            Timber.i(String.valueOf(movie));
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putParcelable(MovieDetailFragment.ARG_MOVIE, movie);
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.ARG_MOVIE, mMovie);

                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((Activity) context, mMovieImage, context.getResources()
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
