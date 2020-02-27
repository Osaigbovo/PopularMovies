package com.osaigbovo.udacity.popularmovies.data.remote;

import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.MovieResponse;
import com.osaigbovo.udacity.popularmovies.data.model.Reviews;
import com.osaigbovo.udacity.popularmovies.data.model.SearchResponse;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface that communicates with TheMovieDB API using Retrofit 2 and RxJava 2.
 *
 * @author Osaigbovo Odiase.
 */
public interface RequestInterface {

    @Headers("Cache-Control:public ,max-age=60")
    @GET("movie/{type}")
    Single<MovieResponse> getMovies(
            @Path("type") String type,
            @Query("api_key") String apiKey,
            @Query("page") int page);

    @GET("movie/{id}?append_to_response=credits,videos")
    Observable<MovieDetail> getMovieDetail(
            @Path("id") int id,
            @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Observable<Reviews> reviews(
            @Path("id") long movieId,
            @Query("api_key") String apiKey,
            @Query("page") int page);

    @GET("search/movie")
    Observable<SearchResponse> search(
            @Query("api_key") String apiKey,
            @Query("query") String query);

}

/*@Headers("Cache-Control:public ,max-age=60")
    @GET("movie/popular")
    Single<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page);

    @Headers("Cache-Control:public ,max-age=60")
    @GET("movie/top_rated")
    Single<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page);*/

