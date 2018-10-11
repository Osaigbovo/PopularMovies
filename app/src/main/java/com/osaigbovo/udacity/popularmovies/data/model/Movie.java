package com.osaigbovo.udacity.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Osaigbovo Odiase.
 */
public class Movie implements Parcelable {

    @NonNull
    @Expose
    @SerializedName("id")
    private Integer id;

    @Expose
    @SerializedName("original_title")
    private String originalTitle;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("poster_path")
    private String posterPath;

    @Expose
    @SerializedName("overview")
    private String overview;

    @Expose
    @SerializedName("release_date")
    private String releaseDate;

    @Expose
    @SerializedName("original_language")
    private String originalLanguage;

    @Expose
    @SerializedName("backdrop_path")
    private String backdropPath;

    @Expose
    @SerializedName("popularity")
    private Double popularity;

    @Expose
    @SerializedName("video")
    private Boolean video;

    @Expose
    @SerializedName("vote_count")
    private Integer voteCount;

    @Expose
    @SerializedName("vote_average")
    private Double voteAverage;

    @Expose
    @SerializedName("adult")
    private boolean adult;

    public Movie(Integer id, String originalTitle, String title, String posterPath,
                 String overview, String releaseDate, String originalLanguage, String backdropPath,
                 Double popularity, Boolean video, Integer voteCount, Double voteAverage,
                 boolean adult) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.originalLanguage = originalLanguage;
        this.backdropPath = backdropPath;
        this.popularity = popularity;
        this.video = video;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
        this.adult = adult;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
        dest.writeString(this.posterPath);
        dest.writeString(this.backdropPath);
        dest.writeDouble(this.popularity);
        dest.writeString(this.title);
        dest.writeDouble(this.voteAverage);
        dest.writeInt(this.voteCount);
    }

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
        this.popularity = in.readDouble();
        this.title = in.readString();
        this.voteAverage = in.readDouble();
        this.voteCount = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
