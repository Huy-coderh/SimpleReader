package com.example.simplereader;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplereader.UI.MyReaderView;
import com.example.simplereader.bookshelf.BaseBook;
import com.example.simplereader.bookshelf.WebBook;
import com.example.simplereader.pagefactory.LocalPageFactory;
import com.example.simplereader.pagefactory.NetPageFactory;
import com.example.simplereader.pagefactory.PageFactory;
import com.example.simplereader.util.DBHelper;
import com.example.simplereader.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends BaseActivity implements View.OnClickListener{

    private PageFactory pageFactory = null;
    private List<PopupWindow> windows = new ArrayList<>();
    private WebBook book = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 设置全屏，覆盖掉状态栏
         */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_reader);

        int origin = getIntent().getIntExtra("book_ori", DBHelper.LOCAL);
        String str = getIntent().getStringExtra("book_str");
        MyReaderView readerView = findViewById(R.id.reader_view);
        //获取屏幕宽高(不包括虚拟按键)
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        final int screenWidth = outMetrics.widthPixels;

        /*触摸翻页事件*/
        readerView.setOnTouchListener((v, event) -> {
            float x = event.getRawX();
            float y = event.getY();
            /*Log.d("x", x + " ");
            Log.d("y", y + " ");*/
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if(x >= screenWidth*2/3){
                        pageFactory.getNextPage();
                    } else if(x <= screenWidth /3){
                        pageFactory.getPrePage();
                    } else {
                        showPopupWindow(v);
                    }
            }
            return true;
        });

        if(origin == DBHelper.NET){
            book = DBHelper.getInstance().getWebBook(str);
            if(book != null && book.getSource() != null){
                pageFactory = new NetPageFactory(readerView);
                pageFactory.setLoadingView(findViewById(R.id.loading));
                pageFactory.openBook(str, book.getSource());
            } else {
                Toast.makeText(this, "书籍打开失败", Toast.LENGTH_SHORT).show();
            }

        } else {
            pageFactory = new LocalPageFactory(readerView);
            pageFactory.openBook(str, Utility.getCharset(str));
        }
    }

    private void showPopupWindow(View view){
        View popup_top = LayoutInflater.from(this).inflate(R.layout.popup_top, null);
        View popup_bottom = LayoutInflater.from(this).inflate(R.layout.popup_bottom, null);

        ImageView popup_img = popup_top.findViewById(R.id.popup_img);     //顶部返回按钮
        TextView popup_text = popup_top.findViewById(R.id.popup_text);      //顶部书名Text
        popup_img.setOnClickListener(this);
        popup_text.setText(book.getName());

        View catalog = popup_bottom.findViewById(R.id.catalogues);
        catalog.setOnClickListener(this);

        PopupWindow popupWindow1 = new PopupWindow(popup_top, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        PopupWindow popupWindow2 = new PopupWindow(popup_bottom, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, false);
        windows.add(popupWindow1);
        windows.add(popupWindow2);
        popupWindow1.setOutsideTouchable(true);
        popupWindow1.setBackgroundDrawable(new ColorDrawable(0x00ffffff));
        popupWindow1.showAtLocation(view, Gravity.START|Gravity.TOP, 0, 0);
        popupWindow2.showAtLocation(view, Gravity.START|Gravity.BOTTOM, 0, 0);
        popupWindow1.setOnDismissListener(() -> {
            for(PopupWindow window : windows){
                window.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pageFactory != null){
            pageFactory.destroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.popup_img :
                finish();
                break;
            case R.id.catalogues :

        }
    }
}
