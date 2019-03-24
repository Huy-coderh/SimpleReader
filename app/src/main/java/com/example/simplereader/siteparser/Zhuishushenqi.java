package com.example.simplereader.siteparser;

import android.app.DownloadManager;
import android.util.Log;

import com.example.simplereader.local.LocalDirectory;
import com.example.simplereader.sitebook.Book;
import com.example.simplereader.sitebook.Chapter;
import com.example.simplereader.util.HttpUtil;
import com.example.simplereader.util.StateCallBack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Zhuishushenqi extends WebSite{

    private String root = "http//www.zhuishushenqi.com";

    @Override
    public void search(String bookName, StateCallBack callBack) {
        String url = null;
        try {
            url = "http://www.zhuishushenqi.com/" + "search?val=" + URLEncoder.encode(bookName, getEncoding());
            Log.d("site", url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
                List<Element> elements = doc.select("body > section > div.content > " +
                        "div.books-list > div");
                List<Book> books = new ArrayList<>();
                for(Element e : elements){
                    String image = e.getElementsByTag("img").attr("src");
                    String name = e.select("div > h4 > a").text();
                    String url = root + e.select("div > h4 > a").attr("href");
                    String author = e.select("div > p.author > span").get(0).text();
                    String type = e.select("div > p.author > span").get(2).text();
                    String intro = e.select("div > p.desc").text();
                    books.add(new Book(name, url, author, image, intro, type, getSiteName()));
                    callBack.onSuccess(books);
                }
            }
        });

    }

    @Override
    public List<Chapter> getCatalog(String url) {
        return null;
    }

    @Override
    public List<String> getContent(String chapterUrl) {
        return null;
    }

    @Override
    public String getSiteName() {
        return "追书神器";
    }

    @Override
    public String getEncoding() {
        return "UTF-8";
    }
}
