package com.dev.nino.erowidreader.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 *
 */
public class Report implements Parcelable, Serializable {

    private String title;

    private String url;

    private String author;

    private String report;

    private String[][] table = new String[0][0];

    private String substances;

    private String age;

    private String pubDate;

    private String views;

    private String id;

    private String weight;

    private String gender;

    private String expYear;

    private int stars;

    private boolean isNew;

    public Report(String title, String url, String author, String report, String[][] table, String substances, String age, String pubDate, String views, String id, String weight, String gender, String expYear, int stars, boolean isNew) {
        this.title = title;
        this.url = url;
        this.author = author;
        this.report = report;
        this.table = table;
        this.substances = substances;
        this.age = age;
        this.pubDate = pubDate;
        this.views = views;
        this.id = id;
        this.weight = weight;
        this.gender = gender;
        this.expYear = expYear;
        this.stars = stars;
        this.isNew = isNew;
    }



    public Report() {

    }


    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(table.length);
        for(String[] arrayDim1 : table) {
            out.writeInt(arrayDim1.length);
            out.writeStringArray(arrayDim1);
        }
        out.writeString(title);
        out.writeString(url);
        out.writeString(author);
        out.writeString(report);
        out.writeString(substances);
        out.writeString(age);
        out.writeString(pubDate);
        out.writeString(views);
        out.writeString(id);
        out.writeString(weight);
        out.writeString(gender);
        out.writeString(expYear);
        out.writeInt(stars);
        out.writeByte((byte) (isNew ? 1 : 0));
    }

    public static final Parcelable.Creator<Report> CREATOR
            = new Parcelable.Creator<Report>() {
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    /** recreate object from parcel */
    private Report(Parcel in) {
        String[][] array = new String[in.readInt()][];
        for(int i = 0; i < array.length; i++) {
            int arraySize = in.readInt();
            array[i] = new String[arraySize];
            in.readStringArray(array[i]);
        }
        table = array;
        this.title = in.readString();
        this.url = in.readString();
        this.author = in.readString();
        this.report = in.readString();
        this.substances = in.readString();
        this.age = in.readString();
        this.pubDate = in.readString();
        this.views = in.readString();
        this.id = in.readString();
        this.weight = in.readString();
        this.gender = in.readString();
        this.expYear = in.readString();
        this.stars = in.readInt();
        this.isNew = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    // TODO not complete
    public void printReport() {
        System.out.println("Title:" + title);
        System.out.println("Url:" + url);
        System.out.println("Id:" + id);
        System.out.println("Author:" + author);
        System.out.println("Pub date:" + pubDate);
        System.out.println("Substances:" + substances);
        System.out.println("New: " + isNew);
        System.out.println("Stars: " + stars);
        System.out.println();
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String[][] getTable() {
        return table;
    }

    public void setTable(String[][] table) {
        this.table = table;
    }

    public String getSubstances() {
        return substances;
    }

    public void setSubstances(String substances) {
        this.substances = substances;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
