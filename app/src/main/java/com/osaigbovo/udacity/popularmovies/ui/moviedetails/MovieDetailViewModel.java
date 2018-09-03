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
            Timber.tag("MovieDetail is a null object");
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
