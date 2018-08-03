package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailFragment;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.osaigbovo.udacity.popularmovies.data.remote.ApiConstants.BASE_IMAGE_URL;
import static com.osaigbovo.udacity.popularmovies.data.remote.ApiConstants.BASE_IMAGE_URL_;
import static com.osaigbovo.udacity.popularmovies.util.ViewUtils.getYearOfRelease;

/**
 * A simple PagedListAdapter that binds Cheese items into CardViews.
 * <p>
 * PagedListAdapter is a RecyclerView.Adapter base class which can present the content of PagedLists
 * in a RecyclerView. It requests new pages as the user scrolls, and handles new PagedLists by
 * computing list differences on a background thread, and dispatching minimal, efficient updates to
 * the RecyclerView to ensure minimal UI thread work.
 * <p>
 * If you want to use your own Adapter base class, try using a PagedListAdapterHelper inside your
 * adapter instead.
 *
 * @see android.arch.paging.PagedListAdapter
 * see android.arch.paging.PagedListAdapterHelper
 */
public class MoviesListAdapter extends PagedListAdapter<TopMovies, MoviesListAdapter.MoviesListViewHolder> {

    private final MoviesListActivity mParentActivity;
    private final boolean mTwoPane;

    MoviesListAdapter(MoviesListActivity parent, boolean twoPane) {
        super(DIFF_CALLBACK);
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @NonNull
    @Override
    public MoviesListAdapter.MoviesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MoviesListAdapter.MoviesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MoviesListAdapter.MoviesListViewHolder holder, int position) {

        String image_url = BASE_IMAGE_URL_ + getItem(position).getPosterPath();

        Timber.i(getItem(position).getOriginalTitle());

        GlideApp.with(holder.itemView.getContext())
                .load(image_url)
                .centerCrop()
                /*.placeholder(R.drawable.ic_movie_empty)
                .error(R.drawable.ic_movie_error)*/
                .into(holder.movieImage);

        holder.movieTitle.setText(getItem(position).getTitle());
        holder.dateText.setText(getYearOfRelease(getItem(position).getReleaseDate()));
        holder.ratingText.setText(String.valueOf(getItem(position).getVoteAverage()));

        holder.itemView.setTag(getItem(position));

        //holder.itemView.setOnClickListener(mOnClickListener);

        holder.itemView.setOnClickListener(view -> {
            TopMovies topMovies = (TopMovies) view.getTag();

            Timber.i(String.valueOf(topMovies));

            if (mTwoPane) {
                Bundle arguments = new Bundle();

                arguments.putParcelable(MovieDetailFragment.ARG_MOVIE_ID, topMovies);
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);

                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.ARG_MOVIE_ID, topMovies);

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context,
                                holder.movieImage,
                                "poster"); //ViewCompat.getTransitionName(holder.movieImage)

                context.startActivity(intent, options.toBundle());
            }
        });
    }

    /*@Override
    public int getItemCount() {
        if (topMovies != null) {
            return topMovies.size();
        } else {
            return 0;
        }
    }*/

    class MoviesListViewHolder extends RecyclerView.ViewHolder {

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
    }

    /**
     * This diff callback informs the PagedListAdapter how to compute list differences when new
     * PagedLists arrive.
     * <p>
     * When you add a Cheese with the 'Add' button, the PagedListAdapter uses diffCallback to
     * detect there's only a single item difference from before, so it only needs to animate and
     * rebind a single view.
     *
     * @see android.support.v7.util.DiffUtil
     */
    private static final DiffUtil.ItemCallback DIFF_CALLBACK = new DiffUtil.ItemCallback<TopMovies>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull TopMovies oldUser, @NonNull TopMovies newUser) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldUser.getId() == newUser.getId();
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull TopMovies oldUser, @NonNull TopMovies newUser) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldUser.equals(newUser);
        }
    };
}
