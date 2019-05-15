package com.example.simplereader.pagefactory;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;

import com.example.simplereader.MyApplication;
import com.example.simplereader.UI.MyReaderView;
import com.example.simplereader.sitebean.Chapter;
import com.example.simplereader.util.BookParser;
import com.example.simplereader.util.DBHelper;
import com.example.simplereader.util.StateCallBack;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网络书籍阅读翻页，跳转管理类
 */
public class NetPageFactory extends PageFactory {

    private static final int LOAD_FAILED = 1;
    private static final int LOAD_SUCCESS = 0;
    private static final int LOAD_PROCESS = 2;

    private int loadState = LOAD_PROCESS;  //目录加载状态

    private String url;
    private String source;
    private int total = 0;    //总章节数
    private int chapter = 0;   //当前章节
    /*begin,end为当前页和下一页的头指针*/
    private int begin = 0;   //当前page开始指针
    private int end = 0;    //当前page结束指针
    private List<Chapter> catalog = null;
    private String lastChapter = "";
    private String currentChapter = "";
    private String nextChapter = "";
    private SparseArray<String> cacheTitle;
    private List<String> content = new ArrayList<>();
    private boolean isForward = true;
    private boolean isWaiting = true;  //当前是否等待翻页
    private CountDownLatch latch;
    private Handler handler = new Handler(Looper.getMainLooper());

    public NetPageFactory(MyReaderView readerView) {
        super(readerView);
    }

    /**
     * 获取章节内容的回调
     */
    private StateCallBack<String> callBack = new StateCallBack<String>() {
        @Override
        public void onSuccess(String s) {

            if(isForward && nextChapter.length() == 0){
                nextChapter = s;
                endLoading(false);
            }
            if(!isForward && lastChapter.length() == 0){
                lastChapter = s;
                endLoading(false);
            }
        }

        @Override
        public void onSucceed() {

        }

        @Override
        public void onFailed(String s) {
            if(isWaiting){
                showMsg(s);
                endLoading(true);
            }
        }

    };

    /**
     * 打开书籍
     * @param url   地址
     * @param sourceSite   来源网站
     */
    @Override
    public void openBook(String url, String sourceSite) {
        loading();
        this.url = url;
        this.source = sourceSite;
        int[] record = DBHelper.getInstance().getWebRecord(url);
        this.total = record[0];
        this.chapter = record[1];
        this.begin = record[2];
        end = begin;
        //从数据库读取章节缓存
        String[] cache = DBHelper.getInstance().getCache(url, chapter);
        if(cache != null && !cache[3].equals("")){
            lastChapter = cache[1];
            currentChapter = cache[3];
            nextChapter = cache[5];
            cacheTitle = new SparseArray<>();
            cacheTitle.put(chapter-1, cache[0]);
            cacheTitle.put(chapter, cache[2]);
            cacheTitle.put(chapter+1, cache[4]);
            endLoading(false);
            BookParser.getInstance().loadCatalog(url, source, new StateCallBack() {
                @Override
                public void onSuccess(Object o) {

                }

                @Override
                public void onSucceed() {
                    loadState = LOAD_SUCCESS;
                    catalog = BookParser.getInstance().getCatalog();
                    total = catalog.size();
                    setTotalNumber();
                    if(isWaiting)
                        skipChapter();
                }

                @Override
                public void onFailed(String s) {
                    loadState = LOAD_FAILED;
                    if(isWaiting) {
                        showMsg("目录加载失败");
                        endLoading(true);
                    }
                }
            });
        } else {
            BookParser.getInstance().loadCatalog(url, source, new StateCallBack() {
                @Override
                public void onSuccess(Object o) {

                }

                @Override
                public void onSucceed() {
                    loadState = LOAD_SUCCESS;
                    catalog = BookParser.getInstance().getCatalog();
                    total = catalog.size();
                    setTotalNumber();
                    //目录加载完成，获取章节
                    ExecutorService threads = Executors.newFixedThreadPool(3);
                    if(chapter >=1 && chapter <= total-2) {
                        latch = new CountDownLatch(3);
                    } else {
                        latch = new CountDownLatch(2);
                    }

                    for(int i=-1; i<=1; i++){
                        int finalI = i;
                        threads.execute(() -> getChapters(chapter+finalI, finalI));
                    }
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    threads.shutdown();
                    setCache();
                    endLoading(false);
                }

                @Override
                public void onFailed(String s) {
                    loadState = LOAD_FAILED;
                    if(isWaiting){
                        showMsg("目录加载失败");
                        endLoading(true);
                    }
                }
            });
        }
    }

    /**
     * 获取上一章节
     */
    @Override
    public void getPrePage() {
        isForward = false;
        if(begin > 0){
            end = begin;
            pageUp();
            printPage(false, false);
            setRecord(chapter, begin);
        } else {
            skipChapter();
        }
    }

    /**
     * 获取下一章节
     */
    @Override
    public void getNextPage() {
        isForward = true;
        if(end < currentChapter.length()){
            begin = end;
            pageDown();
            printPage(true, false);
            setRecord(chapter, begin);
        } else{
            skipChapter();
        }
    }

    /**
     * 跳转上一页
     */
    private void pageUp(){
        content.clear();
        List<String> tempList = new ArrayList<>();
        String paragraph;
        while(begin>0 && freeSize>=fontHeight){
            paragraph = getPreParagraph();
            if(paragraph.length() == 0){
                continue;
            }

            //过滤掉为一串空格的段落
            if(paragraph.replaceAll("\\s", "").length() == 0)
                continue;

            tempList.clear();
            while(paragraph.length()>0 && freeSize>=fontHeight){
                int size = paint.breakText(paragraph, true, pageWidth, null);
                tempList.add(paragraph.substring(0, size));
                //freeSize = freeSize - fontHeight;
                paragraph = paragraph.substring(size);
            }
            //逆序存进content
            if(freeSize >= tempList.size()*fontHeight){
                for(int j=tempList.size(); j>0; j--){
                    content.add(tempList.get(j-1));
                    freeSize -= fontHeight;
                }
                content.add("\r");
            } else{
                int j = tempList.size();
                while(freeSize >= fontHeight && j>0){
                    content.add(tempList.get(j-1));
                    tempList.remove(j-1);
                    freeSize -= fontHeight;
                    j--;
                }
                //剩余，回退指针
                for(String str : tempList){
                    begin += str.length();
                }
                begin ++;
            }
            freeSize -=paragraphSpace;
        }
        freeSize = pageHeight;
    }

    /**
     * 获取上一段落
     * @return  上一段洛String
     */
    private String getPreParagraph(){
        StringBuilder str = new StringBuilder();
        char temp;
        while(begin > 0){
            temp = currentChapter.charAt(--begin);
            if(temp == '$'){
                break;
            }
            str.append(temp);
        }
        return str.reverse().toString();
    }

    /**
     * 跳转带下一页
     */
    private void pageDown(){
        content.clear();
        String paragraph = "";
        while(end<currentChapter.length() && freeSize>=fontHeight){
            paragraph = getNextParagraph();
            if(paragraph.length()==0) continue;

            //过滤掉为一串空格的段落
            if(paragraph.replaceAll("\\s", "").length() == 0){
                paragraph = "";   //置空，防止下面的指针回退
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
        }
        //如有剩余，回退指针
        if(paragraph.length() > 0){
            end -= paragraph.length()+1;
        }
        freeSize = pageHeight;
    }

    /**
     * 获取下一段落
     * @return  下一段洛
     */
    private String getNextParagraph(){
        StringBuilder str = new StringBuilder();
        char temp;
        while( end < currentChapter.length() ){
            temp = currentChapter.charAt(end++);
            if(temp == '$'){
                break;
            }
            str.append(temp);
        }
        return str.toString();
    }

    /**
     * 刷新页面
     * @param isForward  当前跳转方向，向前还是向后
     * @param showMsg  当前是否需要显示错误信息的状态
     */
    private void printPage(boolean isForward, boolean showMsg){
        //清除画布
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if(showMsg && content.size()>0){
            String msg = content.get(0);
            float w = paint.measureText(msg);
            canvas.drawText(msg, marginH+(pageWidth-w)/2, pageHeight/2, paint);
            readerView.setBitmap(bitmap);
            readerView.invalidate();
        } else {
            //绘画顶部标题信息
            if(loadState == LOAD_SUCCESS){
                canvas.drawText(catalog.get(chapter).getTitle(), marginH, marginTop/2, infoPaint);
            }  else {
                String title = cacheTitle.get(chapter);
                if(title != null){
                    canvas.drawText(title, marginH, marginTop/2, infoPaint);
                }
            }
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
            drawProgress( chapter/total);
        }

    }

    /**
     * 进入等待状态的操作
     */
    private void loading(){
        handler.post(() -> {
            if(loadingView != null){
                loadingView.setVisibility(View.VISIBLE);
            }
            readerView.setVisibility(View.INVISIBLE);
        });
    }

    /**
     * 结束等待状态
     * @param showMsg  是否需要显示错误信息
     */
    private synchronized void endLoading(boolean showMsg){
        if(!showMsg && isWaiting){
            if(isForward){
                getNextPage();
            } else {
                getPrePage();
            }
            isWaiting = false;
        }
        handler.post(() -> {
            if(loadingView != null){
                loadingView.setVisibility(View.INVISIBLE);
            }
            readerView.setVisibility(View.VISIBLE);
        });
    }

    /**
     * 章节跳转
     */
    private void skipChapter(){
        if(isForward){
            //如果下一章节内容不为空
            if(nextChapter.length() > 0){
                lastChapter = currentChapter;
                currentChapter = nextChapter;
                nextChapter = "";
                begin = 0;
                end = begin;
                chapter++;
                setCache();
                getNextPage();
                //加载下一章节
                if(loadState==LOAD_SUCCESS && chapter+1 < total){
                    BookParser.getInstance().getContent(chapter+1, callBack);
                }
            } else {   //下一章节为空
                if(loadState==LOAD_SUCCESS ){
                    if(chapter+1 < total){
                        BookParser.getInstance().getContent(chapter+1, callBack);
                        //等待加载完毕
                        isWaiting = true;
                        loading();
                    }
                } else if(loadState == LOAD_PROCESS){
                    isWaiting = true;
                    loading();
                } else if(loadState == LOAD_FAILED){
                    showMsg("目录加载失败");
                }
            }
        } else {
            //前一章节不为空
            if(lastChapter.length() > 0){
                nextChapter = currentChapter;
                currentChapter = lastChapter;
                lastChapter = "";
                begin = currentChapter.length();
                end = begin;
                chapter--;
                setCache();
                getPrePage();
                if(loadState==LOAD_SUCCESS && chapter > 0){
                    BookParser.getInstance().getContent(chapter+1, callBack);
                }
            } else {   //前一章节为空
                if(loadState==LOAD_SUCCESS ){
                    if(chapter > 0){
                        //BookParser.getInstance().getContent(chapter-1, callBack);
                        isWaiting = true;
                        loading();
                    }
                } else if(loadState == LOAD_PROCESS){
                    isWaiting = true;
                    loading();
                } else if(loadState == LOAD_FAILED){
                    showMsg("目录加载失败");
                }
            }
        }
    }

    /**
     * 从网页获取完整的章节内容（上一章，当前章，下一章）
     * @param position   具体章节
     * @param which    上一章，当前章，下一章的标签
     */
    private void getChapters(int position,int which){
        StateCallBack<String> callBack = new StateCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                switch (which){
                    case -1:
                        lastChapter = s;
                        latch.countDown();
                        break;
                    case 0:
                        currentChapter = s;
                        latch.countDown();
                        break;
                    case 1:
                        nextChapter = s;
                        latch.countDown();
                        break;
                }
            }

            @Override
            public void onSucceed() {

            }

            @Override
            public void onFailed(String s) {

            }
        };
        if(position >= 0 && position < total){
            BookParser.getInstance().getContent(position, callBack);
        }
    }

    /**
     * 显示错误信息
     * @param s  需要显示的信息
     */
    private void showMsg(String s){
        content.clear();
        content.add(s);
        printPage(true, true);
    }

    /**
     * 设置阅读记录到数据库
     * @param chapterIndex   阅读章节记录
     * @param record   阅读章节内部阅读点
     */
    private void setRecord(int chapterIndex, int record){
        DBHelper.getInstance().addWebRecord(url, chapterIndex, record);
    }

    /**
     * 将当前的三个章节缓存到数据库
     */
    private void setCache() {
        String title1 = "", title2 = "", title3 = "";
        if(loadState == LOAD_SUCCESS){
            if(chapter>0) title1 = catalog.get(chapter-1).getTitle();
            title2 = catalog.get(chapter).getTitle();
            if(chapter<catalog.size()) title3 = catalog.get(chapter+1).getTitle();
        } else {
            String temp;
            temp = cacheTitle.get(chapter-1);
            if( temp !=null ) title1 = temp;
            temp = cacheTitle.get(chapter);
            if( temp !=null ) title1 = temp;
            temp = cacheTitle.get(chapter+1);
            if( temp !=null ) title1 = temp;
        }
        DBHelper.getInstance().addCache(url, chapter, title1, lastChapter, title2, currentChapter, title3, nextChapter);
    }

    /**
     * 设置当前的总章节数到数据库
     */
    private void setTotalNumber(){
        DBHelper.getInstance().addChapterNumber(url, total);
    }

    @Override
    public void destroy() {
    }
}
