package com.osaigbovo.udacity.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author Osaigbovo Odiase.
 */
public class Credits implements Parcelable {

    @SerializedName("cast")
    private ArrayList<Cast> cast = null;

    @SerializedName("crew")
    private ArrayList<Crew> crew = null;

    public Credits(ArrayList<Cast> cast, ArrayList<Crew> crew) {
        this.cast = cast;
        this.crew = crew;
    }

    public ArrayList<Cast> getCast() {
        return cast;
    }

    public void setCast(ArrayList<Cast> cast) {
        this.cast = cast;
    }

    public ArrayList<Crew> getCrew() {
        return crew;
    }

    public void setCrew(ArrayList<Crew> crew) {
        this.crew = crew;
    }


    protected Credits(Parcel in) {
        if (in.readByte() == 0x01) {
            cast = new ArrayList<Cast>();
            in.readList(cast, Cast.class.getClassLoader());
        } else {
            cast = null;
        }
        if (in.readByte() == 0x01) {
            crew = new ArrayList<Crew>();
            in.readList(crew, Crew.class.getClassLoader());
        } else {
            crew = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (cast == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(cast);
        }
        if (crew == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(crew);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Credits> CREATOR = new Parcelable.Creator<Credits>() {
        @Override
        public Credits createFromParcel(Parcel in) {
            return new Credits(in);
        }

        @Override
        public Credits[] newArray(int size) {
            return new Credits[size];
        }
    };
}
