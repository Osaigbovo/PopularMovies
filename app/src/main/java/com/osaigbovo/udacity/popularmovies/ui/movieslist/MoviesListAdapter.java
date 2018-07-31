package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
import com.osaigbovo.udacity.popularmovies.dummy.DummyContent;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailFragment;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.osaigbovo.udacity.popularmovies.data.remote.ApiConstants.BASE_IMAGE_URL;
import static com.osaigbovo.udacity.popularmovies.util.ViewUtils.getYearOfRelease;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MoviesListViewHolder> {

    private final List<TopMovies> topMovies;
    private final MoviesListActivity mParentActivity;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = view -> {
        /*DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(MovieDetailFragment.ARG_ITEM_ID, item.id);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            mParentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Context context = view.getContext();
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, item.id);

            context.startActivity(intent);
        }*/
    };

    MoviesListAdapter(MoviesListActivity parent, List<TopMovies> mTopMovies, boolean twoPane) {
        mParentActivity = parent;
        topMovies = mTopMovies;
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
        String image_url = BASE_IMAGE_URL + topMovies.get(position).getPosterPath();

        GlideApp.with(holder.itemView.getContext())
                .load(image_url)
                .placeholder(R.drawable.ic_movie_empty)
                .error(R.drawable.ic_movie_error)
                .into(holder.movieImage);

        holder.movieTitle.setText(topMovies.get(position).getTitle());
        holder.dateText.setText(getYearOfRelease(topMovies.get(position).getReleaseDate()));
        holder.ratingText.setText(String.valueOf(topMovies.get(position).getVoteAverage()));

        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        if (topMovies != null) {
            return topMovies.size();
        } else {
            return 0;
        }
    }

    class MoviesListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_movie) ImageView movieImage;
        @BindView(R.id.text_movie_title) TextView movieTitle;
        @BindView(R.id.text_movie_date) TextView dateText;
        @BindView(R.id.text_movie_rating) TextView ratingText;

        MoviesListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
