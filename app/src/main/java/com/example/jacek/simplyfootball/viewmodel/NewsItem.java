package com.example.jacek.simplyfootball.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple POJO class used as a model for a single News object.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class NewsItem implements Parcelable
{
    private String imgsrc;
    private String shortdesc;
    private String title;
    private String link;

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgsrc);
        dest.writeString(this.shortdesc);
        dest.writeString(this.title);
        dest.writeString(this.link);
    }

    public NewsItem() {
    }

    protected NewsItem(Parcel in) {
        this.imgsrc = in.readString();
        this.shortdesc = in.readString();
        this.title = in.readString();
        this.link = in.readString();
    }

    /**
     * Interface that must be implemented and provided as a public CREATOR field that generates
     * instances of Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel source) {
            return new NewsItem(source);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}
