package com.example.simplereader.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.simplereader.MyApplication;

import static android.content.Context.MODE_PRIVATE;

public class SpHelper {

    private SharedPreferences readInfo;
    private SharedPreferences.Editor editor ;

    private static SpHelper instance;

    private SpHelper(){
        readInfo =MyApplication.getContext().getSharedPreferences(
                "read_info", MODE_PRIVATE);
        editor = readInfo.edit();
    }

    public static SpHelper getInstance(){
        if(instance == null){
            synchronized (SpHelper.class){
                if(instance == null){
                    instance = new SpHelper();
                }
            }
        }
        return instance;
    }

    public void setFontSize(int size){
        editor.putInt("font_size", size).apply();
    }

    public void setParagraphSpace(int space){
        editor.putInt("paragraph_space", space).apply();
    }

    public void setNightMode(Boolean isNight){
        editor.putBoolean("night_mode", isNight).apply();
    }

    public void setMark(int start){
        editor.putInt("read_start", start).apply();
    }

    public int getFontSize(){
        return readInfo.getInt("font_size", 0);
    }

    public int getParagraphSpace(){
        return readInfo.getInt("paragraph_space", 0);
    }

    public boolean getNightMode(){
        return readInfo.getBoolean("night_mode", false);
    }

    public int getMark(){
        return readInfo.getInt("read_start",0);
    }
}
