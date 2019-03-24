package com.example.simplereader.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.simplereader.bookshelf.BaseBook;
import com.example.simplereader.bookshelf.LocalBook;
import com.example.simplereader.bookshelf.WebBook;
import com.example.simplereader.db.DBLocalBook;
import com.example.simplereader.db.DBWebBook;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 单例模式
 */
public class BookshelfHelper {

    public static final int ADD_FINISHED = 0;
    public static final int ADD_FAILED = 1;
    public static final int BOOK_EXIST = 2;

    private static BookshelfHelper instance;
    private List<BaseBook> localBaseBookList = new ArrayList<>();
    private List<BaseBook> webBaseBookList = new ArrayList<>();

    private BookshelfHelper(){
        if(localBaseBookList.size() == 0){
            getLocalBooks();
        }
        if(webBaseBookList.size() == 0){
            getWebBooks();
        }
    }

    public static BookshelfHelper getInstance(){
        if(instance == null){
            synchronized (BookshelfHelper.class){
                if(instance == null){
                    instance = new BookshelfHelper();
                }
            }
        }
        return instance;
    }

    private void getLocalBooks(){
        if(localBaseBookList.size() == 0){
            List<DBLocalBook> DBLocalBooks = LitePal.findAll(DBLocalBook.class);
            for(DBLocalBook localBook : DBLocalBooks){
                LocalBook tempBook = new LocalBook(localBook.getName(), localBook.getPath());
                localBaseBookList.add(tempBook);
            }
        }
    }

    private void getWebBooks(){
        if(webBaseBookList.size() == 0){
            List<DBWebBook> DBWebBook = LitePal.findAll(DBWebBook.class);
            for(DBWebBook webBook : DBWebBook){
                WebBook tempBook = new WebBook(webBook.getName(), webBook.getUrl(),
                        webBook.getImage(),webBook.getSource());
                webBaseBookList.add(tempBook);
            }
        }
    }

    public List<BaseBook> getWebBookshelf(){
        return webBaseBookList;
    }

    public List<BaseBook> getLocalBookshelf(){
        return localBaseBookList;
    }

    public int addLocalBook(LocalBook book){
        String str = book.getPath();
        //如果已存在
        if(LitePal.select("path")
                .where("path == ?", str)
                .find(DBLocalBook.class)
                .size() > 0){
            return BOOK_EXIST;
        }
        localBaseBookList.add(book);
        //添加至数据库
        DBLocalBook dbLocalBook = new DBLocalBook();
        dbLocalBook.setName(book.getName());
        dbLocalBook.setPath(book.getPath());
        if(dbLocalBook.save()){
            return ADD_FINISHED;
        }
        return ADD_FAILED;
    }

    public int addWebBook(WebBook book){
        String str = book.getUrl();
        if(LitePal.select("url")
                .where("info == >", str)
                .find(DBWebBook.class)
                .size() > 0){
            return BOOK_EXIST;
        }
        webBaseBookList.add(book);
        DBWebBook dbWebBook = new DBWebBook();
        dbWebBook.setName(book.getName());
        dbWebBook.setImage(book.getImage());
        dbWebBook.setSource(book.getSource());
        dbWebBook.setUrl(book.getUrl());
        if(dbWebBook.save()){
            return ADD_FINISHED;
        }
        return ADD_FAILED;
    }

    public void addLocalRecord(String path, int record){

    }

    public void addWebRecord(String url, int record){}

    public void addChapterRecord(String url, String chapter){}

    private Bitmap getBitmapFromBytes(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    private byte[] getBytesFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

}
