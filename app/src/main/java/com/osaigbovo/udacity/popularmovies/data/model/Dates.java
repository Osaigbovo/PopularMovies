package com.osaigbovo.udacity.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

public class Dates {

    @SerializedName("maximum")
    private String maximum;

    @SerializedName("minimum")
    private String minimum;

    public Dates(String maximum, String minimum) {
        super();
        this.maximum = maximum;
        this.minimum = minimum;
    }

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }
}
