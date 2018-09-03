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
package com.osaigbovo.udacity.popularmovies.di;

import com.osaigbovo.udacity.popularmovies.ui.moviedetails.MovieDetailActivity;
import com.osaigbovo.udacity.popularmovies.ui.movieslist.MoviesListActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components within the app. Add bindings for other sub-components here.
 * @ContributesAndroidInjector was introduced removing the need to:
 * a) Create separate components annotated with @Subcomponent (the need to define @Subcomponent classes.)
 * b) Write custom annotations like @PerActivity.
 */
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract MoviesListActivity contributeMoviesListActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MovieDetailActivity contributeMovieDetailActivity();
}
