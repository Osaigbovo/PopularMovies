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
package com.osaigbovo.udacity.popularmovies.data.remote;

import com.osaigbovo.udacity.popularmovies.PopularMoviesApplication;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Singleton
public class ServiceGenerator {

    private final static String BASE_URL = "http://api.themoviedb.org/3/";

    private static File httpCacheDirectory
            = new File(PopularMoviesApplication.getInstance().getCacheDir(), "responses");
    private static int cacheSize = 30 * 1024 * 1024; // 10 MB
    private static Cache cache = new Cache(httpCacheDirectory, cacheSize);

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new ResponseCacheInterceptor())
            .addInterceptor(new OfflineResponseCacheInterceptor())
            .addInterceptor(new ErrorHandlerInterceptor())
            .cache(cache);

    public static <S> S createService(Class<S> serviceClass) {

        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }

    /**
     * Interceptor to cache data and maintain it for a minute.
     * If the same network request is sent within a minute,
     * the response is retrieved from cache.
     */
    private static class ResponseCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {

            Response originalResponse = chain.proceed(chain.request());
            String cacheControl = originalResponse.header("Cache-Control");

            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                    cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
                Timber.i("Response cache applied");
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + 60)
                        .build();
            } else {
                Timber.i("Response cache not applied");
                return originalResponse;
            }
        }
    }

    /**
     * Interceptor to cache data and maintain it for four weeks.
     * If the device is offline, stale (at most four weeks old)
     * response is fetched from the cache.
     */
    private static class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (!PopularMoviesApplication.hasNetwork()) {
                Timber.i("Offline cache applied");
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            } else {
                Timber.i("Offline cache not applied");
            }
            return chain.proceed(request);
        }
    }

    /**
     * Interceptor to
     */
    private static class ErrorHandlerInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            // TODO: Find a way to display an Error message when any of these response code is returned.
            /*if (response.code() == 500) {
                // M
                return response;
            }*/
            switch (response.code()) {
                case 200:
                    Timber.i("200 - Found");
                    break;
                case 404:
                    Timber.i("404 - Not Found");
                    break;
                case 500:
                    Timber.i("500 - Server Broken");
                    break;
                case 504:
                    Timber.i("500 - Server Broken");
                    break;
                default:
                    Timber.i("Network Unknown Error");
                    //Toast.makeText(ErrorHandlingActivity.this, "not found", Toast.LENGTH_SHORT).show();
                    break;
            }

            return response;
        }
    }
}