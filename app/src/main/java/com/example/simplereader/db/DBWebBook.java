package com.example.simplereader.db;

import org.litepal.crud.LitePalSupport;

public class DBWebBook extends LitePalSupport {

    private int id;

    private String name;

    private String url;

    private String image;

    private String source;

    private String readChapter;

    private int readRecord;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setReadChapter(String readChapter) {
        this.readChapter = readChapter;
    }

    public void setReadRecord(int readRecord) {
        this.readRecord = readRecord;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getReadChapter() {
        return readChapter;
    }

    public int getReadRecord() {
        return readRecord;
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
}
