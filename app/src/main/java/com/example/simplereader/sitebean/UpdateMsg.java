package com.example.simplereader.sitebean;

import java.util.List;

public class UpdateMsg {

    private List<Chapter> chapterList;


    public UpdateMsg(List<Chapter> chapterList){
        this.chapterList = chapterList;
    }

    public List<Chapter> getChapterList() {
        return chapterList;
    }

}
