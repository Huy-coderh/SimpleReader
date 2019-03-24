package com.example.simplereader;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.simplereader.UI.MyReaderView;
import com.example.simplereader.util.PageFactory;

import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends BaseActivity {

    private PageFactory pageFactory;
    private List<PopupWindow> windows = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 设置全屏，覆盖掉状态栏
         */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_reader);
        String bookPath = getIntent().getStringExtra("url_path");
        Log.d("book_path", bookPath);
        MyReaderView myReaderView = findViewById(R.id.reader_view);
        //获取屏幕宽高(不包括虚拟按键)
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        final int screenWidth = outMetrics.widthPixels;

        myReaderView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                        } else if(x <= screenWidth*1/3){
                            pageFactory.getPrePage();
                        } else {
                            showPopupWindow(v);
                        }
                }
                return true;
            }
        });
        pageFactory = new PageFactory(myReaderView);
        pageFactory.openBook(bookPath);
        pageFactory.getNextPage();
    }

    private void showPopupWindow(View view){
        View popup_top = LayoutInflater.from(this).inflate(R.layout.popup_top, null);
        View popup_bottom = LayoutInflater.from(this).inflate(R.layout.popup_bottom, null);
        ImageView popup_img = popup_top.findViewById(R.id.popup_img);
        TextView popup_text = popup_top.findViewById(R.id.popup_text);
        popup_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //popup_text.setText();

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
        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                for(PopupWindow window : windows){
                    window.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageFactory.destory();
    }
}
