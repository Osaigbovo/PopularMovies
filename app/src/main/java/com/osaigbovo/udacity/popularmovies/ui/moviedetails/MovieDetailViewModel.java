package com.osaigbovo.udacity.popularmovies.ui.moviedetails;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSourceFactory;
import com.osaigbovo.udacity.popularmovies.data.model.MovieDetail;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MovieDetailViewModel extends ViewModel {

    private MovieDataSourceFactory movieDataSourceFactory;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<MovieDetail> movieDetailMutableLiveData;

    @Inject
    MovieDetailViewModel(MovieDataSourceFactory movieDataSourceFactory) {
        this.movieDataSourceFactory = movieDataSourceFactory;
        movieDetailMutableLiveData = new MutableLiveData();
    }

    public void getMovie(int id) {
        compositeDisposable.add(movieDataSourceFactory.getMovieDataSource().getMovieDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //.cache()
                .subscribe(this::handleResults, this::handleError)
        );
    }

    private void handleResults(MovieDetail movieDetail) {
        if (movieDetail != null) {
            Timber.tag("Success");
            movieDetailMutableLiveData.postValue(movieDetail);
        } else {
        }
    }

    private void handleError(Throwable e) {
        e.printStackTrace();
        Timber.i("In onError()" + e.getMessage());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        movieDataSourceFactory.getMovieDataSource().clear();
    }
}
