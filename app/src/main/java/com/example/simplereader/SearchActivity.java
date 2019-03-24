package com.example.simplereader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.simplereader.siteparser.Qidian;
import com.example.simplereader.util.BookHelper;

public class SearchActivity extends BaseActivity {

    private BookHelper bookHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);
        bookHelper = new BookHelper(this);
        TextView textView = findViewById(R.id.search);
        EditText editText = findViewById(R.id.edit);
        RecyclerView recyclerView = findViewById(R.id.search_result);
        bookHelper.setRecyclerView(recyclerView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = editText.getText().toString();
                bookHelper.search(key);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bookHelper.detach();
    }
}
