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

import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;
import com.osaigbovo.udacity.popularmovies.data.model.MovieResponse;
import com.osaigbovo.udacity.popularmovies.data.model.Reviews;

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

    @GET("/movie/{id}/reviews")
    Observable<Reviews> reviews(
            @Path("id") long movieId,
            @Query("api_key") String apiKey,
            @Query("page") int page);


}

