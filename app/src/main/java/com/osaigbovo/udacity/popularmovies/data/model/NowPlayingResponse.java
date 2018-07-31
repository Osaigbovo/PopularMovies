package com.osaigbovo.udacity.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NowPlayingResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<NowPlaying> results;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("dates")
    private Dates dates;

    @SerializedName("total_pages")
    private int totalPages;

    public NowPlayingResponse(List<NowPlaying> results, int page, int totalResults, Dates dates, int totalPages) {
        super();
        this.results = results;
        this.page = page;
        this.totalResults = totalResults;
        this.dates = dates;
        this.totalPages = totalPages;
    }

    public List<NowPlaying> getResults() {
        return results;
    }

    public void setResults(List<NowPlaying> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public Dates getDates() {
        return dates;
    }

    public void setDates(Dates dates) {
        this.dates = dates;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
