package com.osaigbovo.udacity.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

public class Genre {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    /**
     * No args constructor for use in serialization
     */
    public Genre() {
    }

    /**
     * @param id
     * @param name
     */
    public Genre(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
