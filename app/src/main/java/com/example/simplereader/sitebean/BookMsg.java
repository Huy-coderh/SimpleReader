package com.example.simplereader.sitebean;

import java.util.List;

public class BookMsg extends UpdateMsg {

    private Book book;

    public BookMsg(Book book, List<Chapter> chapters){
        super(chapters);
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

}
