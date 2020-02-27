package com.osaigbovo.udacity.popularmovies.data.datasource;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.osaigbovo.udacity.popularmovies.BuildConfig;
import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.data.model.MovieResponse;
import com.osaigbovo.udacity.popularmovies.data.remote.RequestInterface;

import javax.inject.Singleton;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;
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

    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();

    private LoadInitialParams<Integer> initialParams;
    private LoadInitialCallback<Integer, Movie> initialCallback;
    private LoadParams<Integer> afterParams;
    private LoadCallback<Integer, Movie> afterCallback;

    private RequestInterface requestInterface;
    private CompositeDisposable compositeDisposable;
    private Completable retryCompletable; //Keep Completable reference for the retry event
    private int pageNumber = 1;
    private String sortType;

    MovieDataSource(RequestInterface requestInterface, String sortType) {
        this.requestInterface = requestInterface;
        this.sortType = sortType;
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

        Timber.e("Fetching %d page: ", pageNumber);
        // Update network states. We also provide an initial load state to the listeners so that
        // the UI can know when the very first list is loaded.
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        // Get the initial Movies from the api
        compositeDisposable.add(requestInterface.getMovies(sortType, API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieResponse -> onMoviesFetched(movieResponse, callback), this::onError));

    }

    private void onMoviesFetched(MovieResponse movieResponse, LoadInitialCallback<Integer, Movie> callback) {
        // Clear retry since request succeeded
        setRetry(null);
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
        compositeDisposable.add(requestInterface.getMovies(sortType, API_KEY, afterParams.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieResponse -> onMoreMoviesFetched(movieResponse, callback), this::onPaginationError)
        );
    }

    private void onMoreMoviesFetched(MovieResponse movieResponse, LoadCallback<Integer, Movie> callback) {
        // clear retry since last request succeeded
        setRetry(null);
        Timber.e(String.format("Fetching %d page: ", afterParams.key));
        //Log.e(TAG, String.format("Fetching %d page: ", movieResponse.getPage()));

        callback.onResult(movieResponse.getResults(), afterParams.key + 1);
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
