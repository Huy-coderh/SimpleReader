package com.example.simplereader.sitebook;

public class Chapter {

    private String chapterName;

    private String ChapterUrl;

    public Chapter(){}

    public Chapter(String chapterName, String chapterUrl){
        this.chapterName = chapterName;
        this.ChapterUrl = chapterUrl;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterUrl() {
        return ChapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        ChapterUrl = chapterUrl;
    }
}
