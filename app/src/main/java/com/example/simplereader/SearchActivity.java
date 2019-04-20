package com.example.simplereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.simplereader.UI.MyLoadingView;
import com.example.simplereader.adapter.ResultRecyclerAdapter;
import com.example.simplereader.sitebean.Book;
import com.example.simplereader.util.BookParser;
import com.example.simplereader.util.StateCallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity {

    //private SearchHelper searchHelper;
    private List<Book> result = new ArrayList<>();
    private ResultRecyclerAdapter adapter;
    private boolean isAttached = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);
        //searchHelper = new SearchHelper();
        TextView textView = findViewById(R.id.search);
        EditText editText = findViewById(R.id.edit);
        MyLoadingView loadingView = findViewById(R.id.loading);
        loadingView.setVisibility(View.INVISIBLE);

        /*
          初始化RecyclerView
         */
        RecyclerView recyclerView = findViewById(R.id.search_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResultRecyclerAdapter(result);
        adapter.setOnItemClickListener((v, position) -> {
            BookParser.getInstance().destroySearch();
            isAttached = false;
            Intent intent = new Intent(SearchActivity.this, BookActivity.class);
            intent.putExtra("book_data", result.get(position));
            intent.putExtra("mode", BookActivity.MODE_UPDATE);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        textView.setOnClickListener(v -> {
            textView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility(View.VISIBLE);
            String key = editText.getText().toString();
            BookParser.getInstance().search(key, new StateCallBack<Book>() {
                @Override
                public void onSuccess(Book book) {
                    runOnUiThread(() -> {
                        if(isAttached)
                            updateResult(book);
                    });
                }

                @Override
                public void onSucceed() {

                }

                @Override
                public void onFailed(String s) {

                }
            });
        });
    }

    /**
     * 更新RecyclerView
     * @param book 搜索到的书籍
     */
    private synchronized void updateResult(Book book){
        result.add(book);
        Collections.sort(result);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BookParser.getInstance().destroySearch();
    }
}
