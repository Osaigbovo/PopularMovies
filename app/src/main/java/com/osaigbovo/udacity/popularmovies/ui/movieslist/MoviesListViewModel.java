package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.data.model.MoviesResult;
import com.osaigbovo.udacity.popularmovies.data.repository.MovieRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MoviesListActivity
 *
 * @author Osaigbovo Odiase.
 */
public class MoviesListViewModel extends ViewModel {

    private static final int pageSize = 20;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<String> sortType = new MutableLiveData<>();

    LiveData<PagedList<Movie>> moviesList;

    private LiveData<MoviesResult> repoMoviesResult;
    private LiveData<List<MovieDetail>> favoriteMoviesLiveData;
    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> refreshState;

    private MovieRepository movieRepository;

    @Inject
    MoviesListViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;

        repoMoviesResult = Transformations.map(sortType, movieRepository::getSortedMovies);

        moviesList = Transformations.switchMap(repoMoviesResult, input -> input.pagedListLiveData);

        networkState = Transformations.switchMap(repoMoviesResult, input -> input.networkState);

        refreshState = Transformations.switchMap(repoMoviesResult, input -> input.networkState);

        // Get a list of Favorite Movies from the Database
        favoriteMoviesLiveData = LiveDataReactiveStreams.fromPublisher(movieRepository.getFavorites()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));
    }

    LiveData<List<MovieDetail>> getFavorites() {
        return favoriteMoviesLiveData;
    }

    LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    LiveData<NetworkState> getRefreshState() {
        return refreshState;
    }

    void addFavorite(MovieDetail movieDetail) {
        Timber.i("Movie Added To Watchlist");
        compositeDisposable.add(Completable.fromAction(() -> movieRepository.addFavorite(movieDetail))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    void removeFavorite(MovieDetail movieDetail) {
        Timber.i("Movie Removed From Watchlist");
        compositeDisposable.add(Completable.fromAction(() -> movieRepository.removeFavorite(movieDetail))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    void sort(String sort) {
        sortType.setValue(sort);
    }

    void refresh() {
        if (repoMoviesResult.getValue().sourceLiveData.getValue() != null) {
            repoMoviesResult.getValue().sourceLiveData.getValue().invalidate();
        }
    }

    public void retry() {
        repoMoviesResult.getValue().sourceLiveData.getValue().retry();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        repoMoviesResult.getValue().sourceLiveData.getValue().clear();
    }
}
