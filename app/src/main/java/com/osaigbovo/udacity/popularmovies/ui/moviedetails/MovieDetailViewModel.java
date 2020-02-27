package com.osaigbovo.udacity.popularmovies.ui.moviedetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.Reviews;
import com.osaigbovo.udacity.popularmovies.data.repository.MovieRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MovieDetailActivity
 *
 * @author Osaigbovo Odiase.
 */
public class MovieDetailViewModel extends ViewModel {

    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<MovieDetail> movieDetailMutableLiveData;
    MutableLiveData<Reviews> reviewsMutableLiveData;

    @Inject
    MovieDetailViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        movieDetailMutableLiveData = new MutableLiveData();
        reviewsMutableLiveData = new MutableLiveData();
    }

    public void getMovieDetails(int id) {
        // TODO - Fetch Reviews for each movie, apply the correct Function.
        compositeDisposable.add(movieRepository.getMovieDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(this::handleResults, this::handleError)
        );
    }

    private void handleResults(MovieDetail movieDetail) {
        if (movieDetail != null) {
            Timber.tag("Success");
            movieDetailMutableLiveData.postValue(movieDetail);
        } else {
            Timber.tag("MovieDetail is a null");
        }
    }

    public void getReviews(int id) {
        compositeDisposable.add(movieRepository.getReviews(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviews -> reviewsMutableLiveData.postValue(reviews), this::handleError)
        );

    }

    private void handleError(Throwable e) {
        e.printStackTrace();
        Timber.i("In onError()" + e.getMessage());
    }

    public LiveData<MovieDetail> isFavorite(int movie_id) {
        Timber.i("Checking if Movie is in the Favorites");
        return movieRepository.isFavorite(movie_id);
    }

    public LiveData<MovieDetail> getFavorite(int movie_id) {
        return movieRepository.getFavorite(movie_id);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
