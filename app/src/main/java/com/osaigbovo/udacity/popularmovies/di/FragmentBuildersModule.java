package com.osaigbovo.udacity.popularmovies.di;

import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/*
* @author Osaigbovo Odiase.
* */
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract MovieDetailFragment contributeMovieDetailFragment();

}
