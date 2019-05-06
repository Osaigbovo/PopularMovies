package com.osaigbovo.udacity.popularmovies.data.datasource;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

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

    public final MutableLiveData<MovieDataSource> movieDataSourceLiveData = new MutableLiveData<>();
    private final RequestInterface requestInterface;
    private final String sortType;

    @Inject
    public MovieDataSourceFactory(RequestInterface requestInterface, String sortType) {
        this.requestInterface = requestInterface;
        this.sortType = sortType;
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
        MovieDataSource movieDataSource = new MovieDataSource(requestInterface, sortType);
        movieDataSourceLiveData.postValue(movieDataSource);
        return movieDataSource;
    }

}
