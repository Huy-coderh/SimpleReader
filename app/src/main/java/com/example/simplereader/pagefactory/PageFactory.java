package com.example.simplereader.pagefactory;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.BatteryManager;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.simplereader.MyApplication;
import com.example.simplereader.R;
import com.example.simplereader.UI.MyLoadingView;
import com.example.simplereader.UI.MyReaderView;
import com.example.simplereader.util.SpHelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class PageFactory {

    int pageWidth;
    int pageHeight;

    int marginH;  //页面距离屏幕边缘px
    int marginTop;
    int marginBottom;
    //int lineSpace = 20;   //每行间距
    int paragraphSpace;   //段落之间间距
    int fontSize;   //子号大小
    int fontHeight;   //字体实际高度(包括边距)

    boolean nightMode;
    int infoSize;   //下方电池时间进度等信息的字体尺寸
    int freeSize;   //当前页面剩余空间

    MyReaderView readerView;
    View loadingView;
    Canvas canvas;
    Paint infoPaint;
    Paint paint;
    Bitmap bitmap;  //page界面

    private SpHelper spHelper;

    PageFactory(MyReaderView readerView){
        this.readerView = readerView;
        init();
    }

    public void setLoadingView(View view){
        this.loadingView = view;
    }

    private void init(){
        //获取屏幕宽高
        DisplayMetrics metrics = MyApplication.getContext().getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        marginH = 50;
        marginTop = 100;
        marginBottom = 140;
        pageWidth = screenWidth - 2* marginH;
        pageHeight = screenHeight - marginBottom - marginTop;
        freeSize = pageHeight;
        paragraphSpace = 60;
        fontSize = 50;
        infoSize = 40;

        spHelper = new SpHelper();

        int temp;
        temp = spHelper.getFontSize();
        if(temp != 0){
            fontSize = temp;
        }
        temp = spHelper.getParagraphSpace();
        if(temp!= 0){
            paragraphSpace = temp;
        }
        nightMode = spHelper.getNightMode();

        bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);  //构造canvas对象对bitmap操作
        infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        infoPaint.setTextSize(infoSize);
        infoPaint.setColor(MyApplication.getContext().getResources().getColor(R.color.reader_info_color1));
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(fontSize);
        fontHeight = getFontHeight();
    }

    /**
     * 获取字体实际高度，包括间隙
     * @return 高度
     */
    private int getFontHeight(){
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }

    /**
     * 绘制电量
     */
    public void drawBattery(){
        //绘制电量
        Intent batteryIntent = MyApplication.getContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int scaledLevel = batteryIntent != null ?
                batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : 0;
        int scale = batteryIntent != null ?
                batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : 0;
        String battery = String.valueOf(scaledLevel*100/scale);
        canvas.drawText("电量:"+battery, marginH+200, pageHeight+marginTop+marginBottom/2, infoPaint);
    }

    /**
     * 绘制时间
     */
    public void drawTime(){
        //绘制时间
        String time = new SimpleDateFormat("HH:mm", Locale.CHINA).format(
                new Date(System.currentTimeMillis()));
        canvas.drawText(""+time, marginH+50, pageHeight+marginTop+marginBottom/2, infoPaint);
    }

    /**
     * 绘制阅读进度
     * @param progress 进度值
     */
    public void drawProgress(float progress){
        //绘制百分比
        DecimalFormat format = new DecimalFormat("#0.00");
        String readingProgress = format.format(progress)+"%";
        canvas.drawText(readingProgress, pageWidth+marginH-150, pageHeight+marginTop+marginBottom/2, infoPaint);

        readerView.setBitmap(bitmap);
        readerView.invalidate();
    }



    public abstract void openBook(String address, String tag);

    public abstract void getPrePage();

    public abstract void getNextPage();

    public abstract void destroy();
}
