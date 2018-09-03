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
package com.osaigbovo.udacity.popularmovies;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.osaigbovo.udacity.popularmovies.di.AppInjector;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;

/*
* Application has activities that is why we implement HasActivityInjector interface.
* Activities have fragments so we have to implement HasFragmentInjector/HasSupportFragmentInjector
* in our activities.
* No child fragment and don’t inject anything in your fragments, no need to implement
* HasSupportFragmentInjector.
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
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
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
