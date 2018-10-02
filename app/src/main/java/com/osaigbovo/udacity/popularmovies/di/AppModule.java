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

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.osaigbovo.udacity.popularmovies.data.local.PopularMoviesDatabase;
import com.osaigbovo.udacity.popularmovies.data.local.dao.FavoriteDao;
import com.osaigbovo.udacity.popularmovies.data.receiver.CheckConnectionBroadcastReceiver;
import com.osaigbovo.udacity.popularmovies.data.remote.RequestInterface;
import com.osaigbovo.udacity.popularmovies.data.remote.ServiceGenerator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Application-wide dependencies.
 */
@Module(includes = ViewModelModule.class)
// Module means the class which contains methods who will provide dependencies.
class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    RequestInterface provideMoviesService() {
        return ServiceGenerator.createService(RequestInterface.class);
    }

    @Singleton
    @Provides
    PopularMoviesDatabase provideDb(Application app) {
        return Room.databaseBuilder(app, PopularMoviesDatabase.class, "opularMovies.db")
                //.addMigrations(PopularMoviesDatabase.MIGRATION_1_2)
                .build();
    }

    @Singleton
    @Provides
    FavoriteDao provideFavMoviesDao(PopularMoviesDatabase db) {
        return db.favoriteDao();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    CheckConnectionBroadcastReceiver providesCheckConnectionBroadcastReceiver() {
        return new CheckConnectionBroadcastReceiver();
    }

    @Provides
    @Singleton
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }

}