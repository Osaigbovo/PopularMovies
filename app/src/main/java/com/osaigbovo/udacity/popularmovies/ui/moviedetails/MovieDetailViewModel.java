/*
 * Copyright 2018.  Osaigbovo Odiase
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.osaigbovo.udacity.popularmovies.ui.moviedetails;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.repository.MovieRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MovieDetailViewModel extends ViewModel {

    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<MovieDetail> movieDetailMutableLiveData;

    @Inject
    MovieDetailViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        movieDetailMutableLiveData = new MutableLiveData();
    }

    public void getMovieDetails(int id) {
        // TODO - Fetch Reviews for each movie, apply the correct Function.
        compositeDisposable.add(movieRepository.getMovieDetails(id)
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
            Timber.tag("MovieDetail is a null object");
        }
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


    // TODO - Completable after adding or deleting a movie, display Snackbar if successful/unsuccessful
}
