package com.example.simplereader.sitebook;

public class Book {
    private String name;  //书名
    private String author;  //作者
    private String url;  //地址
    private String image;  //图片
    private String intro;  //简介
    private String type;  //类型
    private String source;  //来源

    public Book(){}


    public Book(String name, String url, String author, String image, String intro){
        this.name = name;
        this.url = url;
        this.author = author;
        this.image = image;
        this.intro = intro;
    }

    public Book(String name, String url, String author, String image, String intro, String type, String source){
        this.name = name;
        this.url = url;
        this.author = author;
        this.image = image;
        this.intro = intro;
        this.type = type;
        this.source = source;
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
}
