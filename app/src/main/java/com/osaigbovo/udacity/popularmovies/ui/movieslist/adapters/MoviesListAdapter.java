package com.osaigbovo.udacity.popularmovies.ui.movieslist.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListActivity;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListViewHolder;
import com.osaigbovo.udacity.popularmovies.util.NetworkStateViewHolder;
import com.osaigbovo.udacity.popularmovies.util.RetryCallback;

import java.util.Objects;

/**
 * PagedListAdapter is a RecyclerView.Adapter base class which can present the content of PagedLists
 * in a RecyclerView. It requests new pages as the user scrolls, and handles new PagedLists by
 * computing list differences on a background thread, and dispatching minimal, efficient updates to
 * the RecyclerView to ensure minimal UI thread work.
 *
 * @author Osaigbovo Odiase.
 */
public class MoviesListAdapter extends PagedListAdapter<Movie, RecyclerView.ViewHolder> {

    private NetworkState networkState;

    private final RetryCallback retryCallback;
    private final MoviesListActivity mParentActivity;
    private final boolean mTwoPane;

    public MoviesListAdapter(MoviesListActivity parentActivity, RetryCallback retryCallback, boolean twoPane) {
        super(DIFF_CALLBACK);
        this.retryCallback = retryCallback;
        mParentActivity = parentActivity;
        mTwoPane = twoPane;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.item_movie:
                return MoviesListViewHolder.create(parent);
            case R.layout.item_network_state:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.item_movie:
                ((MoviesListViewHolder) holder).bindTo(getItem(position), mParentActivity, mTwoPane);
                break;
            case R.layout.item_network_state:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.item_network_state;
        } else {
            return R.layout.item_movie;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasExtraRow() ? 1 : 0);
    }

    /**
     * Set the current network state to the adapter
     * but this work only after the initial load
     * and the adapter already have list to add new loading raw to it
     * so the initial loading state the activity responsible for handle it
     *
     * @param newNetworkState the new network state
     */
    public void setNetworkState(NetworkState newNetworkState) {

        NetworkState previousState = this.networkState;
        boolean hadExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean hasExtraRow = hasExtraRow();
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }

    }

    /**
     * This diff callback informs the PagedListAdapter how to compute list differences when new
     * PagedLists arrive.
     */
    private static final DiffUtil.ItemCallback DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull Movie oldUser, @NonNull Movie newUser) {
            return Objects.equals(oldUser.getId(), newUser.getId());
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Movie oldUser, @NonNull Movie newUser) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldUser.equals(newUser);
        }
    };
}
