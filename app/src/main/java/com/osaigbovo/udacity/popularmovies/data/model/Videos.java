package com.osaigbovo.udacity.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author Osaigbovo Odiase.
 */
public class Videos implements Parcelable {

    @SerializedName("id")
    @ColumnInfo(name = "video_id")
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

    protected Videos(Parcel in) {
        if (in.readByte() == 0x01) {
            videos = new ArrayList<Video>();
            in.readList(videos, Video.class.getClassLoader());
        } else {
            videos = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (videos == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(videos);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Videos> CREATOR = new Parcelable.Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel in) {
            return new Videos(in);
        }

        @Override
        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };
}
