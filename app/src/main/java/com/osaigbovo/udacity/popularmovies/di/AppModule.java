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
 * Module means the class which contains methods who will provide dependencies.
 *
 * @author Osaigbovo Odiase.
 */
@Module(includes = ViewModelModule.class)
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
