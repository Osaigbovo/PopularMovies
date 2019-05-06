package com.osaigbovo.udacity.popularmovies.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailViewModel;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListViewModel;
import com.osaigbovo.udacity.popularmovies.ui.search.SearchViewModel;
import com.osaigbovo.udacity.popularmovies.viewmodel.MoviesViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/*
 * @author Osaigbovo Odiase.
 * */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MoviesListViewModel.class)
    abstract ViewModel bindMoviesListViewModel(MoviesListViewModel moviesListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel.class)
    abstract ViewModel bindMovieDetailViewModel(MovieDetailViewModel movieDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MoviesViewModelFactory factory);
}
