package com.example.simplereader.siteparser;

import com.example.simplereader.sitebook.Chapter;
import com.example.simplereader.sitebook.SiteBook;
import com.example.simplereader.util.StateCallBack;

import java.util.List;

public abstract class WebSite {

    public abstract void search(String bookName, StateCallBack callBack);

    public abstract List<Chapter> getCatalog(String url);

    public abstract List<String> getContent(String chapterUrl);

    public abstract String getSiteName();

    public String getEncoding(){
        return "GBK";
    }
}
