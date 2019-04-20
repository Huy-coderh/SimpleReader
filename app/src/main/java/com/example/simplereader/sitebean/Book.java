package com.example.simplereader.sitebean;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Comparable<Book>,Parcelable {
    private String name;  //书名
    private String author;  //作者
    private String url;  //地址
    private String image;  //图片
    private String intro;  //简介
    private String type;  //类型
    private String time;  //时间
    private String source;  //来源
    private String encoding;  //编码方式
    private int matchValue;  //匹配度

    public Book(String name, String url, String author, String img, String intro){
        this.name = name;
        this.url = url;
        this.author = author;
        this.image = img;
        this.intro = intro;
    }

    public Book(String name, String url, String author, String image, String intro, String type,
                String time, String source, String encoding){
        this.name = name;
        this.url = url;
        this.author = author;
        this.image = image;
        this.intro = intro;
        this.type = type;
        this.time = time;
        this.source = source;
        this.encoding = encoding;
    }

    public Book(String name, String url, String author, String image, String intro, String type,
                String time, String source, String encoding, int matchValue){
        this.name = name;
        this.url = url;
        this.author = author;
        this.image = image;
        this.intro = intro;
        this.type = type;
        this.time = time;
        this.source = source;
        this.encoding = encoding;
        this.matchValue = matchValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(int matchValue) {
        this.matchValue = matchValue;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int compareTo(Book o) {
        //按匹配度降序排列
        return o.matchValue - this.matchValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.author);
        dest.writeString(this.url);
        dest.writeString(this.image);
        dest.writeString(this.intro);
        dest.writeString(this.type);
        dest.writeString(this.time);
        dest.writeString(this.source);
        dest.writeString(this.encoding);
        dest.writeInt(this.matchValue);
    }

    protected Book(Parcel in) {
        this.name = in.readString();
        this.author = in.readString();
        this.url = in.readString();
        this.image = in.readString();
        this.intro = in.readString();
        this.type = in.readString();
        this.time = in.readString();
        this.source = in.readString();
        this.encoding = in.readString();
        this.matchValue = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
