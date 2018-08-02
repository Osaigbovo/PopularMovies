package com.osaigbovo.udacity.popularmovies.data.remote;

import com.osaigbovo.udacity.popularmovies.data.model.MovieResponse;
import com.osaigbovo.udacity.popularmovies.data.model.NowPlayingResponse;

import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestInterface {

    @GET("movie/now_playing")
    Single<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey);

    @GET("movie/popular")
    Single<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/top_rated")
    Single<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey);

    //@Headers("Cache-Control:public ,max-age=60")
    @GET("movie/top_rated")
    Single<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page);

    @GET("movie/now_playing")
    Single<NowPlayingResponse> getNowPlaying(
            @Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Flowable<NowPlayingResponse> getNowPlaying(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page);

}

