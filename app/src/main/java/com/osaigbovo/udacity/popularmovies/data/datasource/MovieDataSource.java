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
package com.osaigbovo.udacity.popularmovies.data.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.osaigbovo.udacity.popularmovies.BuildConfig;
import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.MovieResponse;
import com.osaigbovo.udacity.popularmovies.data.remote.RequestInterface;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A Paged Data Source that uses the before/after keys returned in page requests for pagination.
 *
 * @author Osaigbovo Odiase.
 */
@Singleton
public class MovieDataSource extends PageKeyedDataSource<Integer, Movie> {

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = "MovieDataSource";

    RequestInterface requestInterface;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();

    private int pageNumber = 1;

    private LoadInitialParams<Integer> initialParams;
    private LoadInitialCallback<Integer, Movie> initialCallback;
    private LoadParams<Integer> afterParams;
    private LoadCallback<Integer, Movie> afterCallback;

    //Keep Completable reference for the retry event
    private Completable retryCompletable;

    String type;

    @Inject
    MovieDataSource(RequestInterface requestInterface) {
        this.requestInterface = requestInterface;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movie> callback) {

    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, Movie> callback) {
        this.initialParams = params;
        this.initialCallback = callback;

        //Log.d(TAG, "Fetching first page: " + pageNumber);
        Timber.e("Fetching %d page: ", pageNumber);
        // Update network states. We also provide an initial load state to the listeners so that
        // the UI can know when the very first list is loaded.
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        // Get the initial Movies from the api
        compositeDisposable.add(requestInterface.getMovies(type, API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieResponse -> onMoviesFetched(movieResponse, callback), this::onError));

    }

    private void onMoviesFetched(MovieResponse movieResponse, LoadInitialCallback<Integer, Movie> callback) {
        // Clear retry since request succeeded
        this.setRetry(null);
        networkState.postValue(NetworkState.LOADED);
        initialLoad.postValue(NetworkState.LOADED);
        callback.onResult(movieResponse.getResults(), null, 2);
    }

    private void onError(Throwable throwable) {
        // Keep a Completable for future retry
        setRetry(() -> loadInitial(initialParams, initialCallback));
        NetworkState error = NetworkState.error(throwable.getMessage());
        // Publish the error
        networkState.postValue(error);
        initialLoad.postValue(error);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, Movie> callback) {
        this.afterParams = params;
        this.afterCallback = callback;

        // set network value to loading.
        networkState.postValue(NetworkState.LOADING);

        // Get the Movies from the API after id
        compositeDisposable.add(requestInterface.getMovies(type, API_KEY, afterParams.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieResponse -> onMoreMoviesFetched(movieResponse, callback), this::onPaginationError)
        );
    }

    private void onMoreMoviesFetched(MovieResponse movieResponse, LoadCallback<Integer, Movie> callback) {
        // clear retry since last request succeeded
        MovieDataSource.this.setRetry(null);

        //pageNumber++;
        Timber.e(String.format("Fetching %d page: ", afterParams.key));
        //Log.e(TAG, String.format("Fetching %d page: ", movieResponse.getPage()));

        callback.onResult(movieResponse.getResults(), afterParams.key+1);
        networkState.postValue(NetworkState.LOADED);
    }

    private void onPaginationError(Throwable throwable) {
        // keep a Completable for future retry
        setRetry(() -> loadAfter(afterParams, afterCallback));
        NetworkState error = NetworkState.error(throwable.getMessage());
        // publish the error
        networkState.postValue(error);
    }

    @NonNull
    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @NonNull
    public MutableLiveData<NetworkState> getInitialLoad() {
        return initialLoad;
    }

    private void setRetry(final Action action) {
        if (action == null) {
            this.retryCompletable = null;
        } else {
            this.retryCompletable = Completable.fromAction(action);
        }
    }

    public void retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                    }, throwable -> Timber.e(throwable.getMessage())));
        }
    }

    public void clear() {
        compositeDisposable.clear();
        pageNumber = 1;
    }

}
