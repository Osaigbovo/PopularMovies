package com.osaigbovo.udacity.popularmovies.di;

import android.app.Application;

import com.osaigbovo.udacity.popularmovies.PopularMoviesApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/*
* Component which actually determines all the modules that has to be used and
* in which classes these dependency injection should work.
*/

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityModule.class, FragmentBuildersModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    // Where the dependency injection has to be used.
    void inject(PopularMoviesApp app);

}
