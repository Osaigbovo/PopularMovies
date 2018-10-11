package com.osaigbovo.udacity.popularmovies.data.remote;

import android.support.annotation.NonNull;

import com.osaigbovo.udacity.popularmovies.PopularMoviesApp;

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

import static com.osaigbovo.udacity.popularmovies.util.AppConstants.BASE_URL;

/**
 * A class that defines how Retrofit 2 & OkHttp should communicate with an API.
 * Interceptors, Caching, Logging
 *
 * @author Osaigbovo Odiase.
 */
@Singleton
public class ServiceGenerator {

    private static File httpCacheDirectory
            = new File(PopularMoviesApp.getInstance().getCacheDir(), "responses");
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
        public Response intercept(@NonNull Chain chain) throws IOException {

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
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();

            if (!PopularMoviesApp.hasNetwork()) {
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
     * Interceptor to display response message
     */
    private static class ErrorHandlerInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
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
                    break;
            }

            return response;
        }
    }
}