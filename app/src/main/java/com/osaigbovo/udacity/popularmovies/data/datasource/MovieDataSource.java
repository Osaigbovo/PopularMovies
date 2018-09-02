package com.osaigbovo.udacity.popularmovies.data.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.osaigbovo.udacity.popularmovies.BuildConfig;
import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.model.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.MovieResponse;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
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

@Singleton
public class MovieDataSource extends ItemKeyedDataSource<Integer, TopMovies> {

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = "ShowsDataSource";

    RequestInterface requestInterface;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<NetworkState> initialLoad;

    private int pageNumber = 1;

    private LoadInitialParams<Integer> initialParams;
    private LoadInitialCallback<TopMovies> initialCallback;
    private LoadParams<Integer> afterParams;
    private LoadCallback<TopMovies> afterCallback;
    //Keep Completable reference for the retry event
    private Completable retryCompletable;

    String type;

    @Inject
    MovieDataSource(RequestInterface requestInterface) {
        this.requestInterface = requestInterface;
        compositeDisposable = new CompositeDisposable();
        networkState = new MutableLiveData<>();
        initialLoad = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<TopMovies> callback) {
        this.initialParams = params;
        this.initialCallback = callback;

        Log.d(TAG, "Fetching first page: " + pageNumber);
        // Update network states. We also provide an initial load state to the listeners so that
        // the UI can know when the very first list is loaded.
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        // Get the initial Movies from the api
        compositeDisposable.add(requestInterface.getMovies(type, API_KEY, pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieResponse -> onMoviesFetched(movieResponse, callback), this::onError));

    }

    private void onMoviesFetched(MovieResponse movieResponse, LoadInitialCallback<TopMovies> callback) {
        // Clear retry since request succeeded
        this.setRetry(null);
        pageNumber++;
        callback.onResult(movieResponse.getResults());
        networkState.postValue(NetworkState.LOADED);
        initialLoad.postValue(NetworkState.LOADED);
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
                          @NonNull LoadCallback<TopMovies> callback) {
        this.afterParams = params;
        this.afterCallback = callback;

        // set network value to loading.
        networkState.postValue(NetworkState.LOADING);

        // Get the Movies from the API after id
        compositeDisposable.add(requestInterface.getMovies(type, API_KEY, params.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieResponse -> onMoreMoviesFetched(movieResponse, callback), this::onPaginationError)
        );
    }

    private void onMoreMoviesFetched(MovieResponse movieResponse, LoadCallback<TopMovies> callback) {
        // clear retry since last request succeeded
        MovieDataSource.this.setRetry(null);
        networkState.postValue(NetworkState.LOADED);
        pageNumber++;
        callback.onResult(movieResponse.getResults());
    }

    private void onPaginationError(Throwable throwable) {
        // keep a Completable for future retry
        setRetry(() -> loadAfter(afterParams, afterCallback));
        NetworkState error = NetworkState.error(throwable.getMessage());
        // publish the error
        networkState.postValue(error);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<TopMovies> callback) {
        //Ignored, since we only ever append to our initial load
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull TopMovies topMovies) {
        return pageNumber;
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

    public Observable<MovieDetail> getMovieDetails(int movieID) {
        return requestInterface.getMovieDetail(movieID, API_KEY);
    }

    public void clear() {
        compositeDisposable.clear();
        pageNumber = 1;
    }

}
