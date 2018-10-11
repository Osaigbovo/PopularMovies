package com.osaigbovo.udacity.popularmovies.data.local.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.osaigbovo.udacity.popularmovies.data.model.Credits;
import com.osaigbovo.udacity.popularmovies.data.model.Genre;
import com.osaigbovo.udacity.popularmovies.data.model.Videos;

import java.util.ArrayList;

/**
 * Entity that represents a table within the database.
 * where fields are columns of the table.
 *
 * @author Osaigbovo Odiase.
 */
@Entity(primaryKeys = "id",
        tableName = "favorite",
        indices = {@Index(value = {"id"}, unique = true)}
)
public class MovieDetail implements Parcelable {

    @NonNull
    @SerializedName("id")
    private int id;

    @SerializedName("imdb_id")
    private String imdbId;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("budget")
    private int budget;

    @SerializedName("genres")
    private ArrayList<Genre> genres;

    @SerializedName("title")
    private String title;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("overview")
    private String overview;

    @SerializedName("popularity")
    private float popularity;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("revenue")
    private int revenue;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("tagline")
    private String tagline;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("adult")
    private boolean adult;

    @SerializedName("video")
    private boolean video;

    @Embedded
    @SerializedName("videos")
    private Videos videos;

    @Embedded
    @SerializedName("credits")
    private Credits credits;

    public MovieDetail(int id, String imdbId, String posterPath, String backdropPath,
                       int budget, ArrayList<Genre> genres, String title, String originalTitle,
                       String overview, float popularity, String releaseDate, int revenue, int runtime,
                       String originalLanguage, String tagline, double voteAverage, int voteCount, boolean adult,
                       boolean video, Videos videos, Credits credits) {
        this.id = id;
        this.imdbId = imdbId;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.budget = budget;
        this.genres = genres;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.revenue = revenue;
        this.runtime = runtime;
        this.originalLanguage = originalLanguage;
        this.tagline = tagline;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.adult = adult;
        this.video = video;
        this.videos = videos;
        this.credits = credits;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public Videos getVideos() {
        return videos;
    }

    public void setVideos(Videos videos) {
        this.videos = videos;
    }

    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    protected MovieDetail(Parcel in) {
        id = in.readInt();
        imdbId = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        budget = in.readInt();
        if (in.readByte() == 0x01) {
            genres = new ArrayList<Genre>();
            in.readList(genres, Genre.class.getClassLoader());
        } else {
            genres = null;
        }
        title = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        popularity = in.readFloat();
        releaseDate = in.readString();
        revenue = in.readInt();
        runtime = in.readInt();
        originalLanguage = in.readString();
        tagline = in.readString();
        voteAverage = in.readDouble();
        voteCount = in.readInt();
        adult = in.readByte() != 0x00;
        video = in.readByte() != 0x00;
        videos = (Videos) in.readValue(Videos.class.getClassLoader());
        credits = (Credits) in.readValue(Credits.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imdbId);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeInt(budget);
        if (genres == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(genres);
        }
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeFloat(popularity);
        dest.writeString(releaseDate);
        dest.writeInt(revenue);
        dest.writeInt(runtime);
        dest.writeString(originalLanguage);
        dest.writeString(tagline);
        dest.writeDouble(voteAverage);
        dest.writeInt(voteCount);
        dest.writeByte((byte) (adult ? 0x01 : 0x00));
        dest.writeByte((byte) (video ? 0x01 : 0x00));
        dest.writeValue(videos);
        dest.writeValue(credits);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieDetail> CREATOR = new Parcelable.Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };

}
