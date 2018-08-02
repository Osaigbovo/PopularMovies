package com.osaigbovo.udacity.popularmovies.data.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.support.annotation.NonNull;

import com.osaigbovo.udacity.popularmovies.data.model.TopMovies;

import io.reactivex.disposables.CompositeDisposable;

public class MovieDataSourceFactory extends DataSource.Factory<Integer, TopMovies> {

    private CompositeDisposable compositeDisposable;

    private MutableLiveData<MovieDataSource> movieDataSourceLiveData;

    public MovieDataSourceFactory(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
        movieDataSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, TopMovies> create() {
        MovieDataSource movieDataSource = new MovieDataSource(compositeDisposable);
        movieDataSourceLiveData.postValue(movieDataSource);
        return movieDataSource;
    }

    @NonNull
    public MutableLiveData<MovieDataSource> getMovieDataSourceLiveData() {
        return movieDataSourceLiveData;
    }
}
