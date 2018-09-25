package com.osaigbovo.udacity.popularmovies.data.local.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.osaigbovo.udacity.popularmovies.data.model.Cast;

import java.util.ArrayList;

public class CastConverter {

    public static String strSeparator = "__,__";

    @TypeConverter
    public static ArrayList<Cast> convertStringToList(String castString) {
        String[] castArray = castString.split(strSeparator);
        ArrayList<Cast> casts = new ArrayList<Cast>();
        Gson gson = new Gson();
        for (int i = 0; i < castArray.length - 1; i++) {
            casts.add(gson.fromJson(castArray[i], Cast.class));
        }
        return casts;
    }

    @TypeConverter
    public static String convertListToString(ArrayList<Cast> cast) {
        Cast[] castArray = new Cast[cast.size()];
        for (int i = 0; i <= cast.size() - 1; i++) {
            castArray[i] = cast.get(i);
        }
        String str = "";
        Gson gson = new Gson();
        for (int i = 0; i < castArray.length; i++) {
            String jsonString = gson.toJson(castArray[i]);
            str = str + jsonString;
            if (i < castArray.length - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }
}
