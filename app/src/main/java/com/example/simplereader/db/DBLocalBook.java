package com.example.simplereader.db;

import org.litepal.crud.LitePalSupport;

public class DBLocalBook extends LitePalSupport {

    private int id;

    private String name;

    private String path;

    private int record;

    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getRecord() {
        return record;
    }
}
