package com.osaigbovo.udacity.popularmovies.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSource;
import com.osaigbovo.udacity.popularmovies.data.datasource.MovieDataSourceFactory;
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

    /*@Provides
    @Singleton
    MovieDataSourceFactory providesMovieDataSourceFactory(MovieDataSource movieDataSource) {
        return new MovieDataSourceFactory(movieDataSource);
    }

    @Provides
    @Singleton
    MovieDataSource providesMovieDataSource(RequestInterface requestInterface) {
        return new MovieDataSource(requestInterface);
    }*/

}
