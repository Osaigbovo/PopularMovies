package com.osaigbovo.udacity.popularmovies.di;

import android.app.Application;
import android.content.Context;

import com.osaigbovo.udacity.popularmovies.data.remote.RequestInterface;
import com.osaigbovo.udacity.popularmovies.data.remote.ServiceGenerator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Application-wide dependencies.
 */
@Module(includes = ViewModelModule.class)
// Module means the class which contains methods who will provide dependencies.
class AppModule {

    @Singleton
    @Provides
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    RequestInterface provideMoviesService() {
        return ServiceGenerator.createService(RequestInterface.class);
    }

//    @Singleton
//    @Provides
//    CinemaDatabase provideDb(Application app) {
//        return Room.databaseBuilder(app, CinemaDatabase.class, "KadaCinemas.db")
//                //.addMigrations(CinemaDatabase.MIGRATION_1_2)
//                .build();
//    }
//
//    @Singleton
//    @Provides
//    TopMoviesDao provideTopMoviesDao(CinemaDatabase db) {
//        return db.topMoviesDao();
//    }

}
