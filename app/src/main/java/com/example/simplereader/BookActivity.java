package com.example.simplereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simplereader.bookshelf.WebBook;
import com.example.simplereader.sitebean.Book;
import com.example.simplereader.sitebean.BookMsg;
import com.example.simplereader.sitebean.Chapter;
import com.example.simplereader.sitebean.UpdateMsg;
import com.example.simplereader.util.BookParser;
import com.example.simplereader.util.DBHelper;
import com.example.simplereader.util.StateCallBack;

import java.util.List;
import java.util.Objects;

public class BookActivity extends BaseActivity implements View.OnClickListener{

    public static final int MODE_ALL = 1;
    public static final int MODE_UPDATE = 2;

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

    private View add;
    private View read;
    private View download;

    private Book book;
    private WebBook webBook;
    private int mode;

    private StateCallBack<UpdateMsg> callBack = new StateCallBack<UpdateMsg>() {
        @Override
        public void onSuccess(UpdateMsg updateMsg) {
            runOnUiThread(() -> initView(updateMsg));
        }

        @Override
        public void onSucceed() {

        }

        @Override
        public void onFailed(String s) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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

        if(getIntent().getIntExtra("mode", MODE_ALL) == MODE_ALL){
            mode = MODE_ALL;
            webBook = getIntent().getParcelableExtra("book_data");
            BookParser.getInstance().getBookInfo(webBook.getUrl(), webBook.getSource(), callBack);
        } else {
            mode = MODE_UPDATE;
            book = getIntent().getParcelableExtra("book_data");
            BookParser.getInstance().getUpdate(book.getUrl(), book.getSource(), callBack);
        }

    }

    private void initView(UpdateMsg updateMsg){
        bookLayout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);

        if(mode == MODE_ALL){
            book = ((BookMsg)updateMsg).getBook();
            if(book.getImage() == null || book.getImage() .equals(""))
                book.setImage(webBook.getImage());
        }

        List<Chapter> chapterList = updateMsg.getChapterList();

        Glide.with(BookActivity.this).load(book.getImage()).into(image);
        nameText.setText(book.getName());
        if(book.getType() != null && !book.getType().equals("")) typeText.setText(book.getType());
        authorText.setText(book.getAuthor());
        sourceText.setText(book.getSource());
        updateText.setText(book.getTime());
        introText.setText(book.getIntro());

        for(int i=chapterList.size()-1,j=0; i>=0; i--,j++){
            if(j < chapters.getChildCount()){
                TextView textView = (TextView) chapters.getChildAt(j);
                textView.setText(chapterList.get(i).getTitle());
                int finalI = i;
                textView.setOnClickListener(v -> {
                /*Intent intent = new Intent(BookActivity.this, ReaderActivity.class);
                intent.putExtra("book_url", latestChapters.get(finalI).attr("href"));
                startActivity(intent);*/
                });
            }
        }
        read = findViewById(R.id.read);
        read.setOnClickListener(this);
        add = findViewById(R.id.add);
        add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.read:
                Intent intent = new Intent(this, ReaderActivity.class);
                intent.putExtra("book_ori", DBHelper.NET);
                intent.putExtra("book_str", book.getUrl());
                startActivity(intent);
                break;
            case R.id.add:
                WebBook webBook = new WebBook(book.getName(), book.getUrl(), book.getImage(), book.getSource());
                switch (DBHelper.getInstance().addWebBook(webBook)){
                    case DBHelper.FINISHED:
                        Toast.makeText(BookActivity.this,
                                "已加入书架", Toast.LENGTH_SHORT).show();
                        break;
                    case DBHelper.FAILED:
                        Toast.makeText(BookActivity.this,
                                "添加失败", Toast.LENGTH_SHORT).show();
                        break;
                    case DBHelper.EXIST:
                        Toast.makeText(BookActivity.this,
                                "书籍已存在", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }
}
