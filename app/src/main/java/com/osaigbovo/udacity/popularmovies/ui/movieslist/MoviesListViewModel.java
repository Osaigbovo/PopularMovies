package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSource;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSourceFactory;
import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;

import io.reactivex.disposables.CompositeDisposable;

public class MoviesListViewModel extends ViewModel {

    LiveData<PagedList<TopMovies>> moviesList;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final int pageSize = 20;

    private MovieDataSourceFactory movieDataSourceFactory;

    public MoviesListViewModel() {
        movieDataSourceFactory = new MovieDataSourceFactory(compositeDisposable);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize * 2)
                .setEnablePlaceholders(false)
                .build();

        moviesList = new LivePagedListBuilder<>(movieDataSourceFactory, config).build();
    }

    public void retry() {
        movieDataSourceFactory.getMovieDataSourceLiveData().getValue().retry();
    }

    public void refresh() {
        movieDataSourceFactory.getMovieDataSourceLiveData().getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(movieDataSourceFactory.getMovieDataSourceLiveData(), MovieDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(movieDataSourceFactory.getMovieDataSourceLiveData(), MovieDataSource::getInitialLoad);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

}
