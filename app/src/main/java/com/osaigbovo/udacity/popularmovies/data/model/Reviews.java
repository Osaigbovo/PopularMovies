package com.osaigbovo.udacity.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author Osaigbovo Odiase.
 */
public class Reviews {

    @SerializedName("id")
    private int id;

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private ArrayList<Review> reviews = null;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public Reviews(int id, int page, ArrayList<Review> reviews, int totalPages, int totalResults) {
        super();
        this.id = id;
        this.page = page;
        this.reviews = reviews;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

}
