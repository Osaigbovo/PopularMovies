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
