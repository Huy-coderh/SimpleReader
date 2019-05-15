package com.example.simplereader.db;

import org.litepal.crud.LitePalSupport;

/**
 * 数据库网络书籍类
 */
public class DBWebBook extends LitePalSupport {

    private int id;

    private String name;   //书名

    private String url;   //书籍地址

    private String image;   //图片地址

    private String source;   //书籍来源

    private int chapter;   //当前阅读章节

    private int record;   //记录当前章节的已阅读位置

    private int total;   //总章节数

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
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

    public String getUrl() {
        return url;
    }

    public int getChapter() {
        return chapter;
    }

    public int getRecord() {
        return record;
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
