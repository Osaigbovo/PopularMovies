package com.osaigbovo.udacity.popularmovies.util;

public class ViewUtils {


    public static String getYearOfRelease(String releaseDate) {
        String[] parts = releaseDate.split("-");
        return parts[0];
    }
}
