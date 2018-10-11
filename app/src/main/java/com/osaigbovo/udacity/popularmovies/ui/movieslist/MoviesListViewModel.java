package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSource;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSourceFactory;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.data.repository.MovieRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MoviesListActivity
 *
 * @author Osaigbovo Odiase.
 */
public class MoviesListViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    LiveData<PagedList<Movie>> moviesList;
    private LiveData<List<MovieDetail>> favoriteMoviesLiveData;
    private static final int pageSize = 20;
    private MovieDataSourceFactory movieDataSourceFactory;
    private MovieRepository movieRepository;
    private PagedList.Config config;

    @Inject
    MoviesListViewModel(MovieDataSourceFactory movieDataSourceFactory, MovieRepository movieRepository) {
        this.movieDataSourceFactory = movieDataSourceFactory;
        this.movieRepository = movieRepository;

        config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize * 2)
                .setEnablePlaceholders(false)
                .build();
        moviesList = new LivePagedListBuilder<>(movieDataSourceFactory, config).build();

        // Get a list of Favorite Movies from the Database
        favoriteMoviesLiveData= LiveDataReactiveStreams.fromPublisher(movieRepository.getFavorites()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));
    }

    public LiveData<List<MovieDetail>> getFavorites() {
        return favoriteMoviesLiveData;
    }

    public void addFavorite(MovieDetail movieDetail) {
        Timber.i("Movie Added To Watchlist");
        compositeDisposable.add(Completable.fromAction(() -> movieRepository.addFavorite(movieDetail))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void removeFavorite(MovieDetail movieDetail) {
        Timber.i("Movie Removed From Watchlist");
        compositeDisposable.add(Completable.fromAction(() -> movieRepository.removeFavorite(movieDetail))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void sort(String text) {
        if (movieDataSourceFactory.getMoDataSourceLiveData().getValue() != null) {
            movieDataSourceFactory.getMoDataSourceLiveData().getValue().invalidate();
        }
        //moviesList.getValue().getDataSource().invalidate();
        movieDataSourceFactory.sort(text);
    }

    public void retry() {
        movieDataSourceFactory.getMoDataSourceLiveData().getValue().retry();
    }

    public void refresh() {
        if (movieDataSourceFactory.getMoDataSourceLiveData().getValue() != null) {
            movieDataSourceFactory.getMoDataSourceLiveData().getValue().invalidate();
        }
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
        compositeDisposable.clear();
        movieDataSourceFactory.getMovieDataSource().clear();
    }

}
