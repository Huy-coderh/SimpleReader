package com.example.simplereader.siteparser;

import com.example.simplereader.sitebook.Book;
import com.example.simplereader.util.HttpUtil;
import com.example.simplereader.util.StateCallBack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ZhuishuRecommend {
    public static final int WEEK = 1;
    public static final int MONTH = 2;
    public static final int TOTAL = 3;

    public static void getRecommend(int which, StateCallBack callBack){
        String root = "http://www.zhuishushenqi.com";
        String url = null;
        switch(which){
            case WEEK :
                url = "http://www.zhuishushenqi.com/ranking/54d42d92321052167dfb75e3?type=male";
                break;
            case MONTH :
                url = "http://www.zhuishushenqi.com/ranking/564d820bc319238a644fb408?type=male";
                break;
            case TOTAL :
                url = "http://www.zhuishushenqi.com/ranking/564d8494fe996c25652644d2?type=male";
                break;
        }
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = new String(response.body().bytes(), getEncoding());
                Document doc = Jsoup.parse(html);
                List<Element> elements = doc.select("body > section > section > " +
                        "div.content > div.books-list > a");
                List<Book> bookList = new ArrayList<>();
                for(Element e : elements){
                    String href = root + e.attr("href");
                    String img = e.getElementsByTag("img").attr("src");
                    String name = e.select("div > h4 > span").text();
                    String author = e.select("div > p").get(0).text();
                    String intro = e.select("div > p").get(1).text();
                    bookList.add(new Book(name, href, author, img, intro));
                    callBack.onSuccess(bookList);
                }
            }
        });
    }

    private static String getEncoding(){
        return "UTF-8";
    }

}
