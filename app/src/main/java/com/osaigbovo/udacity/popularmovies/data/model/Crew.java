package com.osaigbovo.udacity.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Osaigbovo Odiase.
 */
public class Crew implements Parcelable {

    @SerializedName("credit_id")
    private String creditId;

    @SerializedName("department")
    private String department;

    @SerializedName("gender")
    private int gender;

    @SerializedName("id")
    private int id;

    @SerializedName("job")
    private String job;

    @SerializedName("name")
    private String name;

    @SerializedName("profile_path")
    private String profilePath;

    public Crew(String creditId, String department, int gender, int id, String job, String name, String profilePath) {
        this.creditId = creditId;
        this.department = department;
        this.gender = gender;
        this.id = id;
        this.job = job;
        this.name = name;
        this.profilePath = profilePath;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }


    protected Crew(Parcel in) {
        creditId = in.readString();
        department = in.readString();
        gender = in.readInt();
        id = in.readInt();
        job = in.readString();
        name = in.readString();
        profilePath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creditId);
        dest.writeString(department);
        dest.writeInt(gender);
        dest.writeInt(id);
        dest.writeString(job);
        dest.writeString(name);
        dest.writeString(profilePath);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Crew> CREATOR = new Parcelable.Creator<Crew>() {
        @Override
        public Crew createFromParcel(Parcel in) {
            return new Crew(in);
        }

        @Override
        public Crew[] newArray(int size) {
            return new Crew[size];
        }
    };
}