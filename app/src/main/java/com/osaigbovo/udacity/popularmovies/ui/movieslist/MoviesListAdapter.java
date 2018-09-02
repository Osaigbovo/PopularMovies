package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
import com.osaigbovo.udacity.popularmovies.util.NetworkStateViewHolder;
import com.osaigbovo.udacity.popularmovies.util.RetryCallback;

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
public class MoviesListAdapter extends PagedListAdapter<TopMovies, RecyclerView.ViewHolder> {

    private NetworkState networkState;

    private final RetryCallback retryCallback;
    private final MoviesListActivity mParentActivity;
    private final boolean mTwoPane;

    MoviesListAdapter(MoviesListActivity parentActivity, RetryCallback retryCallback, boolean twoPane) {
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
        if (getCurrentList() != null) {
            if (getCurrentList().size() != 0) {
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
