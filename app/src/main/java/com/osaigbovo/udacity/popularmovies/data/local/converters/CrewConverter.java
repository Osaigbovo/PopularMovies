package com.osaigbovo.udacity.popularmovies.data.local.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.osaigbovo.udacity.popularmovies.data.model.Crew;

import java.util.ArrayList;

public class CrewConverter {

    public static String strSeparator = "__,__";

    @TypeConverter
    public static ArrayList<Crew> convertStringToList(String crewString) {
        String[] crewArray = crewString.split(strSeparator);
        ArrayList<Crew> crews = new ArrayList<Crew>();
        Gson gson = new Gson();
        for (int i = 0; i < crewArray.length - 1; i++) {
            crews.add(gson.fromJson(crewArray[i], Crew.class));
        }
        return crews;
    }

    @TypeConverter
    public static String convertListToString(ArrayList<Crew> crew) {
        Crew[] crewArray = new Crew[crew.size()];
        for (int i = 0; i <= crew.size() - 1; i++) {
            crewArray[i] = crew.get(i);
        }
        String str = "";
        Gson gson = new Gson();
        for (int i = 0; i < crewArray.length; i++) {
            String jsonString = gson.toJson(crewArray[i]);
            str = str + jsonString;
            if (i < crewArray.length - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }
}
