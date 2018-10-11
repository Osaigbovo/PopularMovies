package com.osaigbovo.udacity.popularmovies.di;

import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListActivity;
import com.osaigbovo.udacity.popularmovies.ui.search.SearchActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components within the app. Add bindings for other sub-components here.
 * @ContributesAndroidInjector was introduced removing the need to:
 * a) Create separate components annotated with @Subcomponent (the need to define @Subcomponent classes.)
 * b) Write custom annotations like @PerActivity.
 *
 * @author Osaigbovo Odiase.
 */
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract MoviesListActivity contributeMoviesListActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MovieDetailActivity contributeMovieDetailActivity();

    @ContributesAndroidInjector
    abstract SearchActivity contributeSearchActivity();
}
