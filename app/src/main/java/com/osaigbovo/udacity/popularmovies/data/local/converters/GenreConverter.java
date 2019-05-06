package com.osaigbovo.udacity.popularmovies.data.local.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.osaigbovo.udacity.popularmovies.data.model.Genre;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * TypeConverter which persists Genre type into a known database type.
 *
 * @author Osaigbovo Odiase.
 */
public class GenreConverter {

    @TypeConverter
    public static ArrayList<Genre> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Genre>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Genre> genres) {
        Gson gson = new Gson();
        String json = gson.toJson(genres);
        return json;
    }
}
