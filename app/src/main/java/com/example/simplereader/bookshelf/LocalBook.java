package com.example.simplereader.bookshelf;

import android.content.Context;

public class LocalBook extends BaseBook {
    private String path;

    public LocalBook(String name, String path){
        super(name);
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
