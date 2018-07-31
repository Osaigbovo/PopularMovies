package com.osaigbovo.udacity.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<TopMovies> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<TopMovies> getResults() {
        return results;
    }

    public void setResults(List<TopMovies> results) {
        this.results = results;
    }

}
