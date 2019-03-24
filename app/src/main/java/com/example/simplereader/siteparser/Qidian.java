package com.example.simplereader.siteparser;


import android.util.Log;

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

public class Qidian {
   public static void getRecommend(StateCallBack callBack){
       String url = "https://www.qidian.com/rank/recom";
       HttpUtil.sendOkHttpRequest(url, new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               callBack.onFailed();
               e.printStackTrace();
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
               String html = response.body().string();
               Document doc = Jsoup.parse(html);
               List<Element> elements = doc.select("#rank-view-list > div > ul > li");
               List<Book> books = new ArrayList<>();
               for(Element e : elements) {
                   String img = "http:" + e.getElementsByClass("book-img-box").first()
                           .select("a > img")
                           .attr("src");
                   String name = e.getElementsByClass("book-mid-info").first()
                           .select("h4 > a").first()
                           .text();
                   String author = e.getElementsByClass("book-mid-info").first()
                           .getElementsByClass("author").first()
                           .getElementsByClass("name").text();
                   String type = e.getElementsByClass("book-mid-info").first()
                           .select("p > a").get(1).text();
                   String intro = e.getElementsByClass("book-mid-info").first()
                           .getElementsByClass("intro").text();
                   Book mbook = new Book();
                   mbook.setName(name);
                   mbook.setAuthor(author);
                   mbook.setImage(img);
                   mbook.setIntro(intro);
                   mbook.setType(type);
                   books.add(mbook);
                   callBack.onSuccess(books);
               }
           }
       });
   }
}

