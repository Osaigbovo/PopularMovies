package com.osaigbovo.udacity.popularmovies;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.osaigbovo.udacity.popularmovies.di.AppInjector;
import com.squareup.leakcanary.RefWatcher;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;

/**
 * Application level class.
 *
 * @author Osaigbovo Odiase.
 */
public class PopularMoviesApp extends Application implements HasActivityInjector {

    private static PopularMoviesApp instance;

    public static Context context;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        PopularMoviesApp application = (PopularMoviesApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AppInjector.init(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.i("Creating our Application");

        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);*/

        context = getApplicationContext();
    }

    public static PopularMoviesApp getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}

/*
 * Application has activities that is why we implement HasActivityInjector interface.
 * Activities have fragments so we have to implement HasFragmentInjector/HasSupportFragmentInjector
 * in our activities.
 * No child fragment and don’t inject anything in your fragments, no need to implement
 * HasSupportFragmentInjector.
 */
