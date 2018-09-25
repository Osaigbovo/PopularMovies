package com.osaigbovo.udacity.popularmovies.data.local.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.osaigbovo.udacity.popularmovies.data.model.Video;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class VideosConverter {

    @TypeConverter
    public static ArrayList<Video> convertStringToList(String videoString) {
        Type listType = new TypeToken<ArrayList<Video>>() {
        }.getType();
        return new Gson().fromJson(videoString, listType);
    }

    @TypeConverter
    public static String convertListToString(ArrayList<Video> videos) {
        Gson gson = new Gson();
        String json = gson.toJson(videos);
        return json;
    }
}
