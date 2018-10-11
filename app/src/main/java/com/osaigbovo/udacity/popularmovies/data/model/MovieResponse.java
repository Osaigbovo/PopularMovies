package com.osaigbovo.udacity.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Osaigbovo Odiase.
 */
public class MovieResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<Movie> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

}
