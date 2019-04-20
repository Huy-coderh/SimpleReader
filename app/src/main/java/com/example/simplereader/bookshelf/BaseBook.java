package com.example.simplereader.bookshelf;


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
