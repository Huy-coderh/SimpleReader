package com.example.simplereader.util;

import com.example.simplereader.sitebook.Book;

import java.util.List;

public interface StateCallBack {

    void onProcess();

    void onSuccess(List<Book> bookList);

    void onFailed();

}
