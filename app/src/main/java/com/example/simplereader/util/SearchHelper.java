package com.example.simplereader.util;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.simplereader.MyApplication;
import com.example.simplereader.adapter.ResultRecyclerAdapter;
import com.example.simplereader.sitebean.Book;
import com.example.simplereader.siteparser.Binghuo;
import com.example.simplereader.siteparser.WebSite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SearchHelper {

    private ResultRecyclerAdapter adapter;

    private Handler handler;
    private ExecutorService threads;

    private List<Book> result = new ArrayList<>();

    private StateCallBack<Book> callBack = new StateCallBack<Book>() {
        @Override
        public void onSuccess(Book book) {
            updateResult(book);
        }

        @Override
        public void onSucceed() {

        }

        @Override
        public void onFailed(String s) {

        }
    };

    public SearchHelper() {
    }

    public void setRecyclerView(RecyclerView recyclerView){
        if(recyclerView != null){
            recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext()));
            adapter = new ResultRecyclerAdapter(new ArrayList<>());
            adapter.setOnItemClickListener(new ResultRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {

                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private synchronized void updateResult(Book book){
        result.add(book);
        Collections.sort(result);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(adapter != null){
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void search(String name){
        List<WebSite> sites = new ArrayList<>();
        sites.add(new Binghuo());
        handler = new Handler(Looper.getMainLooper());   //主线程
        threads = Executors.newFixedThreadPool(sites.size());
        for(WebSite site : sites){
            threads.execute(new Runnable() {
                @Override
                public void run() {
                    site.search(name, callBack);
                }
            });
        }
    }

    public void detach(){
        if(adapter != null){
            adapter = null;
        }
        if(threads != null){
            threads.shutdownNow();
        }
    }
}
