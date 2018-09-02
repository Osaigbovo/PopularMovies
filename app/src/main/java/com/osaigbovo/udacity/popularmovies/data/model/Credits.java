
package com.osaigbovo.udacity.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Credits {

    @SerializedName("cast")
    private ArrayList<Cast> cast = null;

    @SerializedName("crew")
    private ArrayList<Crew> crew = null;

    public Credits(ArrayList<Cast> cast, ArrayList<Crew> crew) {
        this.cast = cast;
        this.crew = crew;
    }

    public ArrayList<Cast> getCast() {
        return cast;
    }

    public void setCast(ArrayList<Cast> cast) {
        this.cast = cast;
    }

    public ArrayList<Crew> getCrew() {
        return crew;
    }

    public void setCrew(ArrayList<Crew> crew) {
        this.crew = crew;
    }

}
