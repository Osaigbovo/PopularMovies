package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
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

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.ViewHolder> {

    private List<MovieDetail> favMoviesList;
    private final MoviesListActivity mParentActivity;
    private final boolean mTwoPane;

    FavoriteMoviesAdapter(MoviesListActivity parentActivity, boolean twoPane) {
        mParentActivity = parentActivity;
        mTwoPane = twoPane;
    }

    public void addMoviesList(List<MovieDetail> favMoviesList) {
        this.favMoviesList = favMoviesList;
        notifyDataSetChanged();
    }

    public MovieDetail removeItem(int position) {
        //favMoviesList.remove(position);
        return favMoviesList.get(position);
    }

    public void restoreItem(MovieDetail movieDetail, int position) {
        favMoviesList.add(position, movieDetail);
        // notify item added by position
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_favorites, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(position, mParentActivity, mTwoPane);
    }

    @Override
    public int getItemCount() {
        return favMoviesList == null ? 0 : favMoviesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView movieImage;
        @BindView(R.id.movieTitle)
        TextView mt;
        @BindView(R.id.movieDate)
        TextView md;
        @BindView(R.id.movieGenres)
        TextView ge;
        @BindView(R.id.movieDuration)
        TextView du;

        MovieDetail m;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindTo(final int position, MoviesListActivity mParentActivity, boolean mTwoPane) {
            this.m = favMoviesList.get(position);

            mt.setText(m.getTitle()); // Display Movie Title

            String image_url = BASE_IMAGE_URL_ + m.getPosterPath();
            GlideApp.with(itemView.getContext())
                    .load(image_url)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .priority(Priority.HIGH)
                    .skipMemoryCache(false)
                    .fitCenter()
                    .transition(withCrossFade())
                    .into(movieImage);

            md.setText(ViewsUtils.getDate(m.getReleaseDate())); // Display Date
            ge.setText(ViewsUtils.getDisplayGenres(m.getGenres())); // Display Genres
            du.setText(ViewsUtils.getDisplayRuntime(m.getRuntime())); // Display Movie Duration

            itemView.setTag(m);
            /*itemView.setOnClickListener(view -> {
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

                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation((Activity) context, movieImage, context.getResources()
                                    .getString(R.string.transition_name));
                    context.startActivity(intent, options.toBundle());
                }
            });*/
        }
    }
}
