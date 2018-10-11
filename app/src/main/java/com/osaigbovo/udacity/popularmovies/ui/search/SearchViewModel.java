package com.osaigbovo.udacity.popularmovies.ui.search;

import android.arch.lifecycle.ViewModel;

import com.osaigbovo.udacity.popularmovies.data.model.SearchResponse;
import com.osaigbovo.udacity.popularmovies.data.repository.MovieRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * ViewModel for SearchActivity.
 *
 * @author Osaigbovo Odiase.
 */
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
