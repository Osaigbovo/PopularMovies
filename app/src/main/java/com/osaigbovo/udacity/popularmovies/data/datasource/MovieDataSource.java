package com.osaigbovo.udacity.popularmovies.data.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;
import com.osaigbovo.udacity.popularmovies.data.remote.RequestInterface;
import com.osaigbovo.udacity.popularmovies.data.remote.ServiceGenerator;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.osaigbovo.udacity.popularmovies.data.remote.ApiConstants.API_KEY;

public class MovieDataSource extends ItemKeyedDataSource<Integer, TopMovies> {

    private static final String TAG = "ShowsDataSource";

    RequestInterface requestInterface;

    private CompositeDisposable compositeDisposable;

    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();

    private int pageNumber = 1;

    //Keep Completable reference for the retry event
    private Completable retryCompletable;

    public MovieDataSource(CompositeDisposable compositeDisposable) {
        this.requestInterface = ServiceGenerator.createService(RequestInterface.class);
        this.compositeDisposable = compositeDisposable;
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


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<TopMovies> callback) {
        Log.d(TAG, "Fetching first page: " + pageNumber);
        // update network states.
        // we also provide an initial load state to the listeners so that the UI can know when the
        // very first list is loaded.
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        // Get the initial Movies from the api
        compositeDisposable.add(requestInterface.getPopularMovies(API_KEY, pageNumber)
                //.subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieResponse -> {
                            // clear retry since last request succeeded
                            this.setRetry(null);
                            pageNumber++;

                            callback.onResult(movieResponse.getResults());
                            networkState.postValue(NetworkState.LOADED);
                            initialLoad.postValue(NetworkState.LOADED);
                        },
                        throwable -> {
                            // keep a Completable for future retry
                            setRetry(() -> loadInitial(params, callback));
                            NetworkState error = NetworkState.error(throwable.getMessage());
                            // publish the error
                            networkState.postValue(error);
                            initialLoad.postValue(error);
                        }));

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<TopMovies> callback) {

        // set network value to loading.
        networkState.postValue(NetworkState.LOADING);

        // Get the Movies from the API after id
        compositeDisposable.add(requestInterface.getPopularMovies(API_KEY, params.key)
                //.subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieResponse -> {
                            // clear retry since last request succeeded
                            MovieDataSource.this.setRetry(null);
                            networkState.postValue(NetworkState.LOADED);
                            pageNumber++;
                            callback.onResult(movieResponse.getResults());
                        },
                        throwable -> {
                            // keep a Completable for future retry
                            setRetry(() -> loadAfter(params, callback));
                            NetworkState error = NetworkState.error(throwable.getMessage());
                            // publish the error
                            networkState.postValue(error);
                        })
        );
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

}
