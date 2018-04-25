package com.dev.nino.erowidreader.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class ExperienceType implements Parcelable {

    private int experienceTypeID;
    private int substanceID;
    private int reportCount;
    private boolean removed;
    private String url;
    private String name;
    private int userViewCount;

    public ExperienceType(int experienceTypeID, int substanceID, int reportCount, boolean removed, String url, String name, int userViewCount) {
        this.experienceTypeID = experienceTypeID;
        this.substanceID = substanceID;
        this.reportCount = reportCount;
        this.removed = removed;
        this.url = url;
        this.name = name;
        this.userViewCount = userViewCount;
    }

    public int getExperienceTypeID() {
        return experienceTypeID;
    }

    public void setExperienceTypeID(int experienceTypeID) {
        this.experienceTypeID = experienceTypeID;
    }

    public int getSubstanceID() {
        return substanceID;
    }

    public int getUserViewCount() {
        return userViewCount;
    }

    public void setUserViewCount(int userViewCount) {
        this.userViewCount = userViewCount;
    }

    public void setSubstanceID(int substanceID) {
        this.substanceID = substanceID;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected ExperienceType(Parcel in) {
        experienceTypeID = in.readInt();
        substanceID = in.readInt();
        reportCount = in.readInt();
        removed = in.readByte() != 0x00;
        url = in.readString();
        name = in.readString();
        userViewCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(experienceTypeID);
        dest.writeInt(substanceID);
        dest.writeInt(reportCount);
        dest.writeByte((byte) (removed ? 0x01 : 0x00));
        dest.writeString(url);
        dest.writeString(name);
        dest.writeInt(userViewCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ExperienceType> CREATOR = new Parcelable.Creator<ExperienceType>() {
        @Override
        public ExperienceType createFromParcel(Parcel in) {
            return new ExperienceType(in);
        }

        @Override
        public ExperienceType[] newArray(int size) {
            return new ExperienceType[size];
        }
    };
}