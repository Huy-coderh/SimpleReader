package com.example.simplereader.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

public class WebBook extends BaseBook implements Parcelable {
    private String url;
    private String image;
    private String source;


    public WebBook(String name, String url, String image, String source){
        super(name);
        this.url = url;
        this.image = image;
        this.source = source;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getName());
        dest.writeString(this.url);
        dest.writeString(this.image);
        dest.writeString(this.source);
    }

    protected WebBook(Parcel in) {
        super(in.readString());
        this.url = in.readString();
        this.image = in.readString();
        this.source = in.readString();

    }

    public static final Creator<WebBook> CREATOR = new Creator<WebBook>() {
        @Override
        public WebBook createFromParcel(Parcel source) {
            return new WebBook(source);
        }

        @Override
        public WebBook[] newArray(int size) {
            return new WebBook[size];
        }
    };
}
