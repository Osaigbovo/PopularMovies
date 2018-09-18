package com.osaigbovo.udacity.popularmovies.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.osaigbovo.udacity.popularmovies.data.local.dao.FavoriteDao;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.remote.RequestInterface;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import timber.log.Timber;

import static com.osaigbovo.udacity.popularmovies.BuildConfig.API_KEY;

@Singleton
public class MovieRepository {

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





    public LiveData<List<MovieDetail>> getFavorites() {
        return favoriteDao.getFavoriteMovies();
    }

    public Flowable<List<MovieDetail>> getFavoritess() {
        return favoriteDao.getFavoriteMoviess();
    }

    public DataSource.Factory<Integer, MovieDetail> sortASCMovie() {
        return favoriteDao.sortASCMovie();
    }



    @NonNull
    @MainThread
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

}
