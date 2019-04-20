package com.example.simplereader.util;

import com.example.simplereader.bookshelf.BaseBook;
import com.example.simplereader.bookshelf.LocalBook;
import com.example.simplereader.bookshelf.WebBook;
import com.example.simplereader.db.DBLocalBook;
import com.example.simplereader.db.DBWebBook;
import com.example.simplereader.db.ChapterCache;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * 单例模式
 * 管理数据库操作
 */
public class DBHelper {

    public static final int NET = 3;
    public static final int LOCAL = 4;

    public static final int FINISHED = 0;    //成功
    public static final int FAILED = 1;    //失败
    public static final int EXIST = 2;    //存储内容已存在

    private static DBHelper instance;
    private List<BaseBook> localBaseBookList = new ArrayList<>();
    private List<BaseBook> webBaseBookList = new ArrayList<>();

    private DBHelper(){
        if(localBaseBookList.size() == 0){
            getLocalBooks();
        }
        if(webBaseBookList.size() == 0){
            getWebBooks();
        }
    }

    public static DBHelper getInstance(){
        if(instance == null){
            synchronized (DBHelper.class){
                if(instance == null){
                    instance = new DBHelper();
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

    /**
     * 读取网络书籍列表
     * @return    网络书籍列表
     */
    public List<BaseBook> getWebBookshelf(){
        return webBaseBookList;
    }

    /**
     * 读取本地书籍列表
     * @return    本地书籍列表
     */
    public List<BaseBook> getLocalBookshelf(){
        return localBaseBookList;
    }

    /**
     * 存储本地书籍信息
     * @param book    book
     * @return    返回存储状态
     */
    public int addLocalBook(LocalBook book){
        String str = book.getPath();
        //如果已存在
        if(LitePal.select("path")
                .where("path == ?", str)
                .find(DBLocalBook.class)
                .size() > 0){
            return EXIST;
        }
        localBaseBookList.add(book);
        //添加至数据库
        DBLocalBook dbLocalBook = new DBLocalBook();
        dbLocalBook.setName(book.getName());
        dbLocalBook.setPath(book.getPath());
        if(dbLocalBook.save()){
            return FINISHED;
        }
        return FAILED;
    }

    /**
     * 存储网络书籍信息
     * @param book    book
     * @return    返回存储状态
     */
    public int addWebBook(WebBook book){
        String str = book.getUrl();
        if(LitePal.select("url")
                .where("url == ?", str)
                .find(DBWebBook.class)
                .size() > 0){
            return EXIST;
        }
        webBaseBookList.add(book);
        DBWebBook dbWebBook = new DBWebBook();
        dbWebBook.setName(book.getName());
        dbWebBook.setImage(book.getImage());
        dbWebBook.setSource(book.getSource());
        dbWebBook.setUrl(book.getUrl());
        if(dbWebBook.save()){
            return FINISHED;
        }
        return FAILED;
    }

    /**
     * 获取网络书籍对象
     * @param url    地址
     * @return    书籍
     */
    public WebBook getWebBook(String url){
        DBWebBook book = LitePal.where(" url = ?", url).findFirst(DBWebBook.class);
        if(book != null)
            return new WebBook(book.getName(), book.getUrl(), book.getImage(),book.getSource());
        return null;
    }


    public int delLocalBook(LocalBook book){
        return FINISHED;
    }

    public int delWebBook(WebBook book){
        return FINISHED;
    }

    /**
     * 存储章节数目
     * @param url    网络书籍唯一标识符  地址
     * @param number    章节数
     */
    public void addChapterNumber(String url, int number){
        DBWebBook book = new DBWebBook();
        if(number != 0){
            book.setTotal(number);
        } else {
            book.setToDefault("total");
        }
        book.updateAll("url = ?", url);
    }

    /**
     * 储存本地书籍阅读记录
     * @param path    本地书籍唯一标识符，路径
     * @param record    阅读记录点
     */
    public void addLocalRecord(String path, int record){
        DBLocalBook book = new DBLocalBook();
        if(record != 0){
            book.setRecord(record);
        } else {
            book.setToDefault("record");
        }
        book.updateAll("path = ?", path);
    }


    /**
     * 获取本地书籍阅读记录
     * @param path    本地书籍唯一标识符，路径
     * @return    阅读记录点
     */
    public int getLocalRecord(String path){
        DBLocalBook book = LitePal.where("path = ?", path).findFirst(DBLocalBook.class);
        if(book != null) return book.getRecord();
        return 0;
    }

    /**
     * 存储网络书籍阅读记录
     * @param url    书籍地址
     * @param chapter    当前章节号
     * @param record    当前章节阅读点
     */
    public void addWebRecord(String url, int chapter, int record){
        DBWebBook book = new DBWebBook();
        if(chapter !=0){
            book.setChapter(chapter);
        } else {
            book.setToDefault("chapter");
        }
        if(record != 0){
            book.setRecord(record);
        } else {
            book.setToDefault("record");
        }
        book.updateAll("url = ?", url);
    }

    /**
     * 获取网络书籍阅读记录
     * @param url   书籍地址
     * @return    包含章节数,当前章节号,当前章节阅读点  的   int[]
     */
    public int[] getWebRecord(String url){
        DBWebBook book = LitePal.where("url = ?", url).findFirst(DBWebBook.class);
        if(book != null) return new int[]{book.getTotal(), book.getChapter(), book.getRecord()};
        return new int[]{0, 0, 0};
    }

    /**
     * 增加书籍章节缓存，缓存上一章,当前章,下一章
     * @param url  唯一标识符 书籍地址
     * @param index    当前章节index
     * @param lastTitle    上一章title
     * @param last    上一章content
     * @param curTitle    当前章title
     * @param cur    当前章content
     * @param nextTitle    下一章title
     * @param next    下一章content
     */
    public void addCache(String url, int index,
                         String lastTitle, String last,
                         String curTitle, String cur,
                         String nextTitle, String next){
        ChapterCache cache = new ChapterCache();
        if(index != 0){
            cache.setIndex(index);
        } else {
            cache.setToDefault("index");
        }

        cache.setLastTitle(lastTitle);
        cache.setLast(last);
        cache.setCurTitle(curTitle);
        cache.setCur(cur);
        cache.setNextTitle(nextTitle);
        cache.setNext(next);

        //如果记录已存在，则更新
        if(LitePal.select("url")
                .where("url == ?", url)
                .find(ChapterCache.class)
                .size() > 0){
            cache.updateAll("url = ?", url);
        } else {//如果不存在记录，则添加
            cache.setUrl(url);
            cache.save();
        }
    }

    /**
     * 获取章节缓存
     * @param url    书籍地址
     * @param index    书记当前章节序号
     * @return    包含title, content的一个String[]
     */
    public String[] getCache(String url, int index){
        ChapterCache chapter = LitePal.where("url = ?", url).findFirst(ChapterCache.class);
        if(chapter != null && index == chapter.getIndex())
            return new String[]{chapter.getLastTitle(), chapter.getLast(),
                    chapter.getCurTitle(), chapter.getCur(),
                    chapter.getNextTitle(),chapter.getNext()};
        else
            return null;
    }
}
