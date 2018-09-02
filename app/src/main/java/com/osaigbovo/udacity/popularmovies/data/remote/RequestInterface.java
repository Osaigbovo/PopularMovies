package com.osaigbovo.udacity.popularmovies.data.remote;

import com.osaigbovo.udacity.popularmovies.data.model.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.MovieResponse;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestInterface {

    @Headers("Cache-Control:public ,max-age=60")
    @GET("movie/popular")
    Single<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page);

    @Headers("Cache-Control:public ,max-age=60")
    @GET("movie/top_rated")
    Single<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page);

    @Headers("Cache-Control:public ,max-age=60")
    @GET("movie/{type}")
    Single<MovieResponse> getMovies(@Path("type") String type,
                                    @Query("api_key") String apiKey,
                                    @Query("page") int page);

    @GET("movie/{id}?append_to_response=credits,videos")
    Observable<MovieDetail> getMovieDetail(
            @Path("id") int id, @Query("api_key") String apiKey);

}

