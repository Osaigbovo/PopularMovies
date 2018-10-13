package com.osaigbovo.udacity.popularmovies.data.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.support.annotation.NonNull;

import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.data.remote.RequestInterface;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A Paged Data Source Factory provides a way to create and observe the last created data source.
 *
 * @author Osaigbovo Odiase
 */
@Singleton
public class MovieDataSourceFactory extends DataSource.Factory<Integer, Movie> {

    RequestInterface requestInterface;

    private MovieDataSource movieDataSource;
    private MutableLiveData<MovieDataSource> movieDataSourceLiveData;
    private String type;

    @Inject
    MovieDataSourceFactory(MovieDataSource movieDataSource, RequestInterface requestInterface) {
        this.movieDataSource = movieDataSource;
        this.requestInterface = requestInterface;
        movieDataSourceLiveData = new MutableLiveData<>();
    }

    /*
     * After calling mDataSource.invalidate() method, mDataSource will be invalidated and the
     * new DataSource instance will be created via DataSourceFactory.create() method,
     * so its important to provide new DataSource() instance every time inside
     * DataSourceFactory.create() method, do not provide same DataSource instance every time.
     * mDataSource.invalidate() is not working, because after invalidation,
     * DataSourceFactory provides the same, already invalidated DataSource instance.
     * */
    @Override
    public DataSource<Integer, Movie> create() {
        movieDataSource = new MovieDataSource(requestInterface);
        movieDataSource.type = type;
        movieDataSourceLiveData.postValue(movieDataSource);
        return movieDataSource;
    }

    @NonNull
    public MutableLiveData<MovieDataSource> getMoDataSourceLiveData() {
        return movieDataSourceLiveData;
    }

    public MovieDataSource getMovieDataSource() {
        return movieDataSource;
    }

    // Sort between Top Rated and Popular movies from the API Endpoint.
    public void sort(String text) {
        type = text;
    }

}
