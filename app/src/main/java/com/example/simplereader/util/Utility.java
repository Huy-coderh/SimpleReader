package com.example.simplereader.util;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Utility {

    /**
     * 获取文件编码方式
     * @param filePath 文件路径
     * @return 编码方式的String形式
     */
    public static String getCharset(String filePath) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(filePath));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

    /**
     * 获取文件编码方式
     * @param filepath 文件路径
     * @return 编码方式的String形式
     */
    public static String getEncode(String filepath){
        String str = null;
        File file = new File(filepath);
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            is.mark(0);
            byte[] first3Bytes = new byte[3];
            is.read(first3Bytes, 0, 3);
            if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {// utf-8
                str = "utf-8";
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {//unicode
                str ="unicode";
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {//utf-16be
                str = "utf-16be";
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFF) {//utf-16le
                str = "utf-16le";
            } else {    //GBK
                str = "GBK";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }


    /**
     * 文件大小单位换算,最多换到到G
     * @param length 文件的字节大小
     * @return 带上单位的文件大小(String)
     */
    public static String getFileLength(long length){
        if(length < 1024){
            return length + "B";
        } else {
            if(length < 1024*1024){
                return Math.round((double)length/1024*100)/100 + "K";
            } else if(length < 1024*1024*1024){
                return Math.round((double)length/1024/1024*100)/100 + "M";
            } else {
                return Math.round((double)length/1024/1024/1024*100)/100 + "G";
            }
        }
    }


    /**
     *
     * 获取搜索关键字和结果之间的匹配度
     * @param search 关键字
     * @param bookName 书名
     * @param author 作者
     * @return 匹配度
     */
    public static int getMatchValue(String search, String bookName, String author){
        if(search.equals(bookName)) return 100;
        if(search.equals(author)) return 99;
        int m = LCS(search, bookName, 0, 0), n = LCS(search, author, 0, 0);
        if(m > 0 || n>0){
            return Math.max(80 - 2*(bookName.length()-m), 80 - 11*(author.length()-n));
        }
        return 0;
    }

    private static int LCS(String s1,String s2,int i,int j){
        if(i>=s1.length()||j>=s2.length()) return 0;
        if(s1.charAt(i)==s2.charAt(j)) return LCS(s1,s2,i+1,j+1)+1;
        else
            return Math.max(LCS(s1,s2,i+1,j), LCS(s1,s2,i,j+1));
    }
}

