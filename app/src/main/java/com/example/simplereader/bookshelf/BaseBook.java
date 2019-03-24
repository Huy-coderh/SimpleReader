package com.example.simplereader.bookshelf;

import android.content.Context;
import android.graphics.Bitmap;

public class BaseBook {
    private String name;

    public BaseBook (String name){
        this.name = name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
