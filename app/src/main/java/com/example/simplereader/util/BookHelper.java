package com.example.simplereader.util;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.simplereader.UI.MyReaderView;
import com.example.simplereader.adapter.ResultRecyclerAdapter;
import com.example.simplereader.sitebook.Book;
import com.example.simplereader.siteparser.WebSite;
import com.example.simplereader.siteparser.Zhuishushenqi;

import java.util.ArrayList;
import java.util.List;

public class BookHelper {

    private Activity activity;
    private MyReaderView myReaderView;
    private ResultRecyclerAdapter adapter;

    private StateCallBack callBack = new StateCallBack() {
        @Override
        public void onProcess() {

        }

        @Override
        public void onSuccess(List<Book> bookList) {
            if(activity != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updataData(bookList);
                    }
                });
            }
        }

        @Override
        public void onFailed() {

        }
    };

    public BookHelper(Activity activity){
        this.activity = activity;
    }

    public void setActivity(Activity mActivity) {
        this.activity = mActivity;
    }

    public void setRecyclerView(RecyclerView recyclerView){
        if(recyclerView != null){
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            adapter = new ResultRecyclerAdapter(new ArrayList<>());
            recyclerView.setAdapter(adapter);
        }
    }

    public void setMyReaderView(MyReaderView myReaderView){
        this.myReaderView = myReaderView;
    }

    public void search(String name){
        List<WebSite> sites = new ArrayList<>();
        new Zhuishushenqi().search(name, callBack);
    }

    public void detach(){
        activity = null;
        myReaderView = null;
    }
}
