package com.example.simplereader.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.simplereader.MyApplication;
import com.example.simplereader.UI.MyReaderView;
import com.google.common.primitives.Bytes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageFactory {


    private int pageWidth;
    private int pageHeight;
    private int margin = 50;  //页面距离屏幕边缘px
    private int lineSpace = 20;   //每行间距
    private int paragraphSpace = 60;   //段落之间间距
    private int fontSize = 50;   //子号大小
    private int fontHeight;   //字体实际高度(包括边距)
    private boolean nightMode;
    private int infoSize = 50;   //下方电池时间进度等信息的尺寸
    private int freeSize;   //当前页面剩余空间

    private MyReaderView pageView;

    private SpHelper spHelper;

    private RandomAccessFile rafile = null;
    private MappedByteBuffer mappedBuffer = null;

    private String encode;
    private int begin = 0;
    private int end = 0;
    private int fileLength;

    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;

    private List<String> content = new ArrayList<>();

    public PageFactory(MyReaderView view){
        this.pageView = view;
        spHelper = SpHelper.getInstance();
        //获取屏幕宽高
        DisplayMetrics metrics = MyApplication.getContext().getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        if(spHelper.getFontSize() != 0){
            fontSize = spHelper.getFontSize();
        }
        if(spHelper.getParagraphSpace() != 0){
            paragraphSpace = spHelper.getParagraphSpace();
        }
        nightMode = spHelper.getNightMode();
        bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        pageView.setBitmap(bitmap);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(fontSize);
        fontHeight = getFontHeight();
        canvas = new Canvas(bitmap);
        pageWidth = screenWidth - 2*margin;
        pageHeight = screenHeight - 2*margin;
        freeSize = pageHeight;
    }

    public void openBook(final String bookPath){
        encode = Utility.getCharset(bookPath);
        Log.d("encode", encode);
        begin = spHelper.getMark();
        end = begin;
        File file = new File(bookPath);
        fileLength = (int) file.length();
        try {
            rafile = new RandomAccessFile(file, "r");
            mappedBuffer = rafile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MyApplication.getContext(), "打开失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void getNextPage(){
        if(end > fileLength){
            return;
        }
        begin = end;
        pageDown();
        printPage(true);
    }

    public void getPrePage(){
        if(begin <= 0){
            return;
        }
        end = begin;
        pageUp();
        printPage(false);
    }

    private void pageUp(){
        content.clear();
        List<String> tempList = new ArrayList<>();
        String paragraph = "";
        while(begin>0 && freeSize>fontHeight){
            try{
                paragraph = new String(getPreParagraph(), encode);
                if(paragraph.equals("")){
                    continue;
                }
                paragraph = paragraph.replaceAll("\r\n","  ");
                paragraph = paragraph.replaceAll("\n", "  ");
                if(paragraph.equals("\r")){
                    /*if(freeSize > paragraphSpace){
                        content.add("\r");
                    }*/
                    continue;
                }
                tempList.clear();
                while(paragraph.length()>0 && freeSize>=fontHeight){
                    int size = paint.breakText(paragraph, true, pageWidth, null);
                    tempList.add(paragraph.substring(0, size));
                    //freeSize = freeSize - fontHeight;
                    paragraph = paragraph.substring(size);
                }
                if(freeSize >= tempList.size()*fontHeight){
                    for(int i=tempList.size(); i>0; i--){
                        content.add(tempList.get(i-1));
                        freeSize -= fontHeight;
                    }
                    content.add("\r");
                } else{
                    int i = tempList.size();
                    while(freeSize >= fontHeight && i>0){
                        content.add(tempList.get(i-1));
                        tempList.remove(i-1);
                        freeSize -= fontHeight;
                        i--;
                    }
                    //剩余，回退指针
                    for(String str : tempList){
                        begin += str.getBytes(encode).length;
                    }
                    begin ++;
                }
                freeSize -=paragraphSpace;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        freeSize = pageHeight;
    }

    private void pageDown(){
        content.clear();
        String paragraph = "";
        while(end<fileLength && freeSize>=fontHeight){
            try {
                paragraph = new String(getNextParagraph(), encode);
                if(paragraph.equals("")){
                    continue;
                }
                paragraph = paragraph.replaceAll("\r\n", "  ");
                paragraph = paragraph.replaceAll("\n", "  ");
                if(paragraph.equals("\r")){
                    continue;
                }
                while(paragraph.length()>0 && freeSize>=fontHeight){
                    int size = paint.breakText(paragraph, true, pageWidth, null);
                    content.add(paragraph.substring(0, size));
                    freeSize = freeSize - fontHeight;
                    paragraph = paragraph.substring(size);
                }
                content.add("\r");
                freeSize -= paragraphSpace;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //如有剩余，回退指针
        if(paragraph.length() > 0){
            try {
                end -= (paragraph).getBytes(encode).length + 1;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        freeSize = pageHeight;
    }

    private byte[] getNextParagraph(){
        List<Byte> bytes = new ArrayList<>();
        byte temp;
        while( end < fileLength ){
            temp = mappedBuffer.get(end++);
            if(temp == (byte)0x0a){
                break;
            }
            bytes.add(temp);
        }
        return Bytes.toArray(bytes);
    }

    private byte[] getPreParagraph(){
        List<Byte> bytes = new ArrayList<>();
        byte temp;
        while( begin > 0){
            temp = mappedBuffer.get(--begin);
            if(temp == (byte)0x0a){
                //begin++;
                break;
            }
            bytes.add(temp);
        }
        Collections.reverse(bytes);
        return Bytes.toArray(bytes);
    }

    private void printPage(boolean isforward){
        //清除画布
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if(content.size() > 0){
            if(!isforward){
                Collections.reverse(content);
            }
            int y = margin;
            for(String line : content){
                if( ! line.equals("\r")){
                    y += fontHeight;
                    canvas.drawText(line, margin, y, paint);
                } else if(y > margin){
                    y += paragraphSpace;
                }
            }
        }
        pageView.setBitmap(bitmap);
        pageView.invalidate();

    }

    public void destory(){
        if(rafile != null){
            try {
                rafile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getFontHeight(){
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }

}
