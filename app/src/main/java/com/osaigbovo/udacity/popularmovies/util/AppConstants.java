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
package com.osaigbovo.udacity.popularmovies.util;

public class AppConstants {

    // API Constants
    public static final String language = "en-US";
    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String BASE_IMAGE_URL_ = "https://image.tmdb.org/t/p/w185";
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w342";
    public static final String BASE_IMAGE_URLs = "https://image.tmdb.org/t/p/w342";
    //"w92", "w154", "w185", "w342", "w500", "w780", or "original"
    public static final String BASE_BACKDROP_URL = "https://image.tmdb.org/t/p/w500";

    // Sort Constants
    public static final String SORT_BY_POPULAR = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final String SORT_BY_FAVORITE = "favorite";

    // Shared Preferences Constants
    public static final String PREF_FILE = "Preferences";
    public static final String PREF_FILTER = "pref_filter";

}
