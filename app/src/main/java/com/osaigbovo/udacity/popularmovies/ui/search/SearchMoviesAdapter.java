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
package com.osaigbovo.udacity.popularmovies.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.osaigbovo.udacity.popularmovies.util.ViewsUtils;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.osaigbovo.udacity.popularmovies.util.AppConstants.BASE_IMAGE_URL_;

public class SearchMoviesAdapter extends RecyclerView.Adapter<SearchMoviesAdapter.SearchViewHolder> {

    private List<Movie> searchMoviesList;
    private final SearchActivity mParentActivity;
    private final boolean mTwoPane;

    SearchMoviesAdapter(SearchActivity parentActivity, boolean twoPane) {
        mParentActivity = parentActivity;
        mTwoPane = twoPane;
    }

    void setSearchMoviesList(List<Movie> searchMoviesList) {
        this.searchMoviesList = searchMoviesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new SearchViewHolder(inflater.inflate(R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.bindTo(position, mParentActivity, mTwoPane);
    }

    @Override
    public int getItemCount() {
        return searchMoviesList == null ? 0 : searchMoviesList.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_movie)
        ImageView movieImage;
        @BindView(R.id.text_movie_title)
        TextView mt;
        @BindView(R.id.text_movie_date)
        TextView md;

        Movie m;

        private SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindTo(final int position, SearchActivity mParentActivity, boolean mTwoPane) {
            this.m = searchMoviesList.get(position);

            String image_url = BASE_IMAGE_URL_ + m.getPosterPath();
            GlideApp.with(itemView.getContext())
                    .load(image_url)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .priority(Priority.HIGH)
                    .skipMemoryCache(false)
                    //.placeholder(R.drawable.ic_movie_empty)
                    .fitCenter()
                    .transition(withCrossFade())
                    //.error(R.drawable.ic_movie_error)
                    .into(movieImage);

            mt.setText(m.getTitle()); // Display Movie Title

            if (!TextUtils.isEmpty(m.getReleaseDate())) {
                md.setText(ViewsUtils.getDate(m.getReleaseDate()));
            } // Display Date

            itemView.setTag(m);
            itemView.setOnClickListener(view -> {
                Movie mMovie = (Movie) view.getTag();
                Timber.i(String.valueOf(m));
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(MovieDetailFragment.ARG_MOVIE, mMovie);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailFragment.ARG_MOVIE, mMovie);

                   /* ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation((Activity) context, movieImage, context.getResources()
                                    .getString(R.string.transition_name));*/
                    context.startActivity(intent/*, options.toBundle()*/);
                }
            });
        }
    }

}
