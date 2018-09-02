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

import javax.inject.Inject;

public class MoviesListViewModel extends ViewModel {

    LiveData<PagedList<TopMovies>> moviesList;
    private static final int pageSize = 20;
    private MovieDataSourceFactory movieDataSourceFactory;
    private PagedList.Config config;

    @Inject
    MoviesListViewModel(MovieDataSourceFactory movieDataSourceFactory) {
        this.movieDataSourceFactory = movieDataSourceFactory;

        config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize * 2)
                .setEnablePlaceholders(false)
                .build();

        //createFilteredUsers();

    }

    public void sort(String text) {
        /*
        * After modification CouponListDataSourceFactory will be looked like in below smple,
        * and call to mCouponListDataSourceFactory.dataSource.invalidate() method will make a refresh,
        * alternatively instead of keeping dataSource instance inside the factory, we can call
        * invalidate method on LiveData< PagedList < CouponModel > >.getValue().getDataSource().invalidate()
        * */
        movieDataSourceFactory.sort(text);

        if(movieDataSourceFactory.getMoDataSourceLiveData().getValue() != null){
            movieDataSourceFactory.getMoDataSourceLiveData().getValue().invalidate();
        }

        //moviesList.getValue().getDataSource().invalidate();

        createFilteredUsers();
    }

    private void createFilteredUsers() {
        moviesList = new LivePagedListBuilder<>(movieDataSourceFactory, config).build();
    }

    public void retry() {
        movieDataSourceFactory.getMoDataSourceLiveData().getValue().retry();
    }

    public void refresh() {
        movieDataSourceFactory.getMoDataSourceLiveData().getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(movieDataSourceFactory.getMoDataSourceLiveData(), MovieDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(movieDataSourceFactory.getMoDataSourceLiveData(), MovieDataSource::getInitialLoad);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        movieDataSourceFactory.getMovieDataSource().clear();
    }

}
