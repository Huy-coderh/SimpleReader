package com.example.simplereader.pagefactory;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.BatteryManager;

import com.example.simplereader.MyApplication;
import com.example.simplereader.UI.MyReaderView;
import com.example.simplereader.util.DBHelper;
import com.google.common.primitives.Bytes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 本地书本阅读翻页，跳转管理类
 */
public class LocalPageFactory extends PageFactory {

    private RandomAccessFile raFile = null;
    private MappedByteBuffer mappedBuffer = null;

    private String encode;
    private int begin = 0;
    private int end = 0;
    private int fileLength;

    private String bookName;
    private String filePath;
    private List<String> content = new ArrayList<>();

    public LocalPageFactory(MyReaderView view){
        super(view);
    }

    @Override
    public void openBook(String bookPath, String encoding){
        this.filePath = bookPath;
        encode = encoding;
        begin = DBHelper.getInstance().getLocalRecord(filePath);
        end = begin;
        File file = new File(bookPath);
        bookName = file.getName();
        fileLength = (int) file.length();
        try {
            raFile = new RandomAccessFile(file, "r");
            mappedBuffer = raFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(MyApplication.getContext(), "打开失败", Toast.LENGTH_SHORT).show();
        }
        getNextPage();
    }

    @Override
    public void getNextPage(){
        if(end >= fileLength){
            return;
        }
        begin = end;
        pageDown();
        printPage(true);
        setRecord();
    }

    @Override
    public void getPrePage(){
        if(begin <= 0){
            return;
        }
        end = begin;
        pageUp();
        printPage(false);
        setRecord();
    }

    private void pageUp(){
        content.clear();
        List<String> tempList = new ArrayList<>();
        String paragraph;
        while(begin>0 && freeSize>=fontHeight){
            try{
                paragraph = new String(getPreParagraph(), encode);
                paragraph = paragraph.replaceAll("\r\n","  ");
                paragraph = paragraph.replaceAll("\n", "  ");
                if(paragraph.equals("") || paragraph.equals("\r")){
                    continue;
                }
                tempList.clear();
                while(paragraph.length()>0 && freeSize>=fontHeight){
                    int size = paint.breakText(paragraph, true, pageWidth, null);
                    tempList.add(paragraph.substring(0, size));
                    //freeSize = freeSize - fontHeight;
                    paragraph = paragraph.substring(size);
                }
                //逆序存进content集合
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

    private void printPage(boolean isForward){
        //清除画布
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        canvas.drawText(bookName, marginH, marginTop/2, infoPaint);

        if(content.size() > 0){
            if(!isForward){
                Collections.reverse(content);
            }
            int y = marginTop;
            for(String line : content){
                if( ! line.equals("\r")){
                    y += fontHeight;
                    canvas.drawText(line, marginH, y, paint);
                } else if(y > marginTop){
                    y += paragraphSpace;
                }
            }
        }

        drawBattery();
        drawTime();
        drawProgress( begin/ fileLength);

        readerView.setBitmap(bitmap);
        readerView.invalidate();

    }

    private void setRecord(){
        DBHelper.getInstance().addLocalRecord(filePath, begin);
    }

    @Override
    public void destroy(){
        if(raFile != null){
            try {
                raFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
