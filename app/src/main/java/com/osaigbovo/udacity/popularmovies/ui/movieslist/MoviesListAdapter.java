package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.dummy.DummyContent;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailFragment;

import java.util.List;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.ViewHolder> {

    private final MoviesListActivity mParentActivity;
    private final List<DummyContent.DummyItem> mValues;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
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
            }
        }
    };

    MoviesListAdapter(MoviesListActivity parent,
                      List<DummyContent.DummyItem> items,
                      boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public MoviesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new MoviesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesListAdapter.ViewHolder holder, int position) {
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = (TextView) view.findViewById(R.id.id_text);
            mContentView = (TextView) view.findViewById(R.id.content);
        }
    }

}
