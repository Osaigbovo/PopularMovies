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
package com.osaigbovo.udacity.popularmovies.ui.movieslist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.osaigbovo.udacity.popularmovies.data.NetworkState;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSource;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSourceFactory;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;

import javax.inject.Inject;

public class MoviesListViewModel extends ViewModel {

    LiveData<PagedList<Movie>> moviesList;
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
                .build();/**/
        //createFilteredUsers();
    }

    public void sort(String text) {
        movieDataSourceFactory.sort(text);

        if(movieDataSourceFactory.getMoDataSourceLiveData().getValue() != null){
            movieDataSourceFactory.getMoDataSourceLiveData().getValue().invalidate();
        }
        //or moviesList.getValue().getDataSource().invalidate();

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
