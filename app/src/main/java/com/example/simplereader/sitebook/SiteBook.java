package com.example.simplereader.sitebook;

public class SiteBook extends Book{

    private String lastChapter;  //书本最新章节

    private String lastChapterUrl;  //书本最新章节地址

    private String lastUpdateTime;  //书本最新更新时间

    private String size;  //字数

    public SiteBook(){}

    public SiteBook(String name, String url, String image, String type, String author,
                    String size, String intro, String lastChapter, String lastChapterUrl,
                    String time, String source){
        super(name, url, author, image, intro, type, source);
        this.lastChapter = lastChapter;
        this.lastChapterUrl = lastChapterUrl;
        this.lastUpdateTime = time;
        this.size = size;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getLastChapterUrl() {
        return lastChapterUrl;
    }

    public void setLastChapterUrl(String lastChapterUrl) {
        this.lastChapterUrl = lastChapterUrl;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
