package com.example.simplereader.siteparser;

import com.example.simplereader.sitebean.Book;
import com.example.simplereader.sitebean.BookMsg;
import com.example.simplereader.sitebean.Chapter;
import com.example.simplereader.sitebean.UpdateMsg;
import com.example.simplereader.util.StateCallBack;

import java.util.List;

public abstract class WebSite {

    public abstract void search(String bookName, StateCallBack<Book> callBack);

    public abstract void getCatalog(String url, StateCallBack<List<Chapter>> callBack);

    public abstract void getContent(String chapterUrl, StateCallBack<String> callBack);

    public abstract void getBookInfo(String url, StateCallBack<UpdateMsg> stateCallBack);

    public abstract void getUpdateInfo(String url, StateCallBack<UpdateMsg> stateCallBack);

    public abstract  String getSiteName();

    public String getEncoding(){
        return "GBK";
    }
}
