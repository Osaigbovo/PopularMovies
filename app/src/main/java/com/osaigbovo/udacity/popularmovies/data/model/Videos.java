package com.osaigbovo.udacity.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Videos {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private ArrayList<Video> videos = null;

    public Videos(int id, ArrayList<Video> videos) {
        this.id = id;
        this.videos = videos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }
}
