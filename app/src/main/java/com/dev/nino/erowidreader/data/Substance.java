package com.dev.nino.erowidreader.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 *
 */
public class Substance implements Serializable, Parcelable {


    private String name;
    private String subText;


    private boolean removed = false;


    private int reportCount = 0;
    private int userViewCount = 0;

    public Substance(String name, String subText, boolean removed, int reportCount, int userViewCount) {
        this.name = name;
        this.subText = subText;
        this.removed = removed;
        this.reportCount = reportCount;
        this.userViewCount = userViewCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubText() {
        return subText;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public int getUserViewCount() {
        return userViewCount;
    }

    public void setUserViewCount(int userViewCount) {
        this.userViewCount = userViewCount;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    protected Substance(Parcel in) {
        name = in.readString();
        subText = in.readString();
        removed = in.readByte() != 0x00;
        reportCount = in.readInt();
        userViewCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(subText);
        dest.writeByte((byte) (removed ? 0x01 : 0x00));
        dest.writeInt(reportCount);
        dest.writeInt(userViewCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Substance> CREATOR = new Parcelable.Creator<Substance>() {
        @Override
        public Substance createFromParcel(Parcel in) {
            return new Substance(in);
        }

        @Override
        public Substance[] newArray(int size) {
            return new Substance[size];
        }
    };
}