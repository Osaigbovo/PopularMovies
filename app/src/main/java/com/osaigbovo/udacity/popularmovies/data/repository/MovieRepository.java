package com.osaigbovo.udacity.popularmovies.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSource;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSourceFactory;
import com.osaigbovo.udacity.popularmovies.data.local.dao.FavoriteDao;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.data.model.MoviesResult;
import com.osaigbovo.udacity.popularmovies.data.model.Reviews;
import com.osaigbovo.udacity.popularmovies.data.model.SearchResponse;
import com.osaigbovo.udacity.popularmovies.data.remote.RequestInterface;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import timber.log.Timber;

import static com.osaigbovo.udacity.popularmovies.BuildConfig.API_KEY;

/**
 * Repository that acts as a mediators between different data sources; API network and ROOM database.
 * It abstracts the data sources from the rest of the app
 *
 * @author Osaigbovo Odiase.
 */
@Singleton
public class MovieRepository {

    private static final int pageSize = 20;

    private final FavoriteDao favoriteDao;
    RequestInterface requestInterface;

    @Inject
    MovieRepository(FavoriteDao favoriteDao, RequestInterface requestInterface) {
        this.favoriteDao = favoriteDao;
        this.requestInterface = requestInterface;
    }

    public Observable<MovieDetail> getMovieDetails(int movieID) {
        return requestInterface.getMovieDetail(movieID, API_KEY);
    }

    public Observable<Reviews> getReviews(int movieID) {
        return requestInterface.reviews(movieID, API_KEY, 1);
    }

    public Observable<SearchResponse> getSearch(String query) {
        return requestInterface.search(API_KEY, query);
    }

    public LiveData<List<MovieDetail>> getFavoritess() {
        return favoriteDao.getFavoriteMoviess();
    }

    public Flowable<List<MovieDetail>> getFavorites() {
        return favoriteDao.getFavoriteMovies();
    }

    public LiveData<MovieDetail> isFavorite(int movieid) {
        return favoriteDao.isFavoriteMovie(movieid);
    }

    public LiveData<MovieDetail> getFavorite(int id) {
        return favoriteDao.getFavoriteMovie(id);
    }

    public void addFavorite(MovieDetail movieDetail) {
        Timber.i("Adding %s to database", movieDetail.getOriginalTitle());
        favoriteDao.saveFavoriteMovie(movieDetail);
    }

    public void removeFavorite(MovieDetail movieDetail) {
        Timber.i("Removing %s to database", movieDetail.getOriginalTitle());
        favoriteDao.deleteFavoriteMovie(movieDetail);
    }

    public MoviesResult getSortedMovies(String sortBy) {
        MovieDataSourceFactory movieDataSourceFactory =
                new MovieDataSourceFactory(requestInterface, sortBy);

        // Paging configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize * 2)
                .setEnablePlaceholders(true)
                .build();

        // Get the paged list
        LiveData<PagedList<Movie>> moviesPagedList = new LivePagedListBuilder<>(movieDataSourceFactory, config).build();;

        LiveData<NetworkState> networkState = Transformations.switchMap(movieDataSourceFactory.movieDataSourceLiveData,
                MovieDataSource::getNetworkState);

        LiveData<NetworkState> refreshState = Transformations.switchMap(movieDataSourceFactory.movieDataSourceLiveData,
                MovieDataSource::getInitialLoad);

        return new MoviesResult(
                moviesPagedList,
                networkState,
                refreshState,
                movieDataSourceFactory.movieDataSourceLiveData
        );
    }

}
