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
package com.osaigbovo.udacity.popularmovies.ui.search;

import android.arch.lifecycle.ViewModel;

import com.osaigbovo.udacity.popularmovies.data.model.SearchResponse;
import com.osaigbovo.udacity.popularmovies.data.repository.MovieRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class SearchViewModel extends ViewModel {

    private MovieRepository movieRepository;

    @Inject
    SearchViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Observable<SearchResponse> search(final String query) {
        return movieRepository
                .getSearch(query)
                /*.delay(2, TimeUnit.SECONDS)*/;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
