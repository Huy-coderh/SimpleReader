package com.example.simplereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simplereader.util.HttpUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BookActivity extends BaseActivity {

    private View bookLayout;
    private View loading;

    private ImageView image;
    private TextView nameText;
    private TextView typeText;
    private TextView authorText;
    private TextView sourceText;
    private TextView updateText;
    private TextView introText;
    private LinearLayout chapters;
    //private List<String> chapterUrls = new ArrayList<>();

    private ImageView add;
    private View read;
    private View download;

    private String sitesName;
    private String encoding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String url = getIntent().getStringExtra("book_url");
        sitesName = getIntent().getStringExtra("book_source");
        if(sitesName.equals("追书神器")){
            encoding = "UTF-8";
        } else {
            encoding = "GBK";
        }

        bookLayout = findViewById(R.id.book_info_layout);
        loading = findViewById(R.id.loading);
        image = findViewById(R.id.image);
        nameText = findViewById(R.id.name);
        typeText = findViewById(R.id.type);
        authorText = findViewById(R.id.author);
        sourceText = findViewById(R.id.source);
        updateText = findViewById(R.id.update);
        introText = findViewById(R.id.intro);
        chapters = findViewById(R.id.latest_chapters);

        bookLayout.setVisibility(View.INVISIBLE);
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = new String(response.body().bytes(), encoding);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView(html);
                    }
                });
            }
        });
    }

    private void initView(String html){
        bookLayout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);

        Document doc = Jsoup.parse(html);
        Element e = doc.select("body > div.page-detail-container.clearfix > div.detail-left").first();
        String img = e.getElementsByClass("book-info").first()
                .getElementsByTag("img").first().attr("src");
        String name = e.select("div.book-info > div > h1").text();
        String type = e.select("div.book-info > div > p:nth-child(3)").text().split("\\|")[1];
        String author = e.select("div.book-info > div > p:nth-child(3) > a").text();
        String source = sitesName;
        String update = e.select("div.book-info > div > p:nth-child(4)").text();
        String intro = e.select("div:nth-child(3) > p").text();
        List<Element> latestChapters = e.select("div:nth-child(4) > ul > li");
        Glide.with(BookActivity.this).load(img).into(image);
        nameText.setText(name);
        typeText.setText(type);
        authorText.setText(author);
        sourceText.setText(source);
        updateText.setText(update);
        introText.setText(intro);
        for(int i=0; i<chapters.getChildCount(); i++){
            TextView textView = (TextView) chapters.getChildAt(i);
            textView.setText(latestChapters.get(i).text());
            int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BookActivity.this, ReaderActivity.class);
                    intent.putExtra("book_url", latestChapters.get(finalI).attr("href"));
                    startActivity(intent);
                }
            });
        }
    }
}
