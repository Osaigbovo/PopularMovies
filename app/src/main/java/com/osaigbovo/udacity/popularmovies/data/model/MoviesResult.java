package com.osaigbovo.udacity.popularmovies.data.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSource;

/**
 * @author Osaigbovo Odiase
 */
public class MoviesResult {
    public LiveData<PagedList<Movie>> pagedListLiveData;
    public LiveData<NetworkState> networkState;
    public LiveData<NetworkState> refreshState;
    public MutableLiveData<MovieDataSource> sourceLiveData;

    public MoviesResult(LiveData<PagedList<Movie>> pagedListLiveData, LiveData<NetworkState> networkState, LiveData<NetworkState> refreshState,
                        MutableLiveData<MovieDataSource> sourceLiveData) {
        this.pagedListLiveData = pagedListLiveData;
        this.networkState = networkState;
        this.refreshState = refreshState;
        this.sourceLiveData = sourceLiveData;
    }
}
