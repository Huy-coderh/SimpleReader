package com.example.simplereader.siteparser;


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

public class Binghuo extends WebSite {

    private final String root = "https://www.bhzw.cc/";

    @Override
    public void search(String bookName, StateCallBack callBack){
        String rhef = "https://www.bhzw.cc/modules/article/search.php";
        RequestBody requestBody = null;
        try {
            requestBody = new FormBody.Builder()
                    .addEncoded("searchkey", URLEncoder.encode(bookName, getEncoding()))
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtil.sendOkHttpRequest(rhef, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = new String(response.body().bytes(), getEncoding());
                Document doc = Jsoup.parse(html);
                List<Element> elements = doc.select("body > div.wrap > div > div.container-bd > " +
                        "div.mod.mod-clean.pattern-update-list > div > table > tbody > tr");
                List<Book> books = new ArrayList<>();
                for(Element e : elements){
                    String name = e.getElementsByClass("name").first().text();
                    String url = root + e.getElementsByClass("name").first().attr("href");
                    String image = getImageUrl(url);
                    String type = e.getElementsByClass("tag").first().text();
                    String author = e.getElementsByClass("author").first().text();
                    //String size = e.getElementsByTag("td").get(3).text();
                    String intro = getIntro(url);
                    //String lastChapter = e.getElementsByClass("chapter").first().text();
                    //String lastChapterUrl = e.getElementsByClass("chapter").first().attr("href");
                    //String time = e.getElementsByClass("time").first().text();
                    books.add(new Book(name, url, author, image, intro, type, getSiteName()));
                }
                callBack.onSuccess(books);
            }
        });


    }

    @Override
    public List<Chapter> getCatalog(String url) {
        String response = HttpUtil.getWebResponse(url, getEncoding());
        Document doc = Jsoup.parse(response);
        List<Element> elements = doc.select("body > div:nth-child(2) > div > div > " +
                "div.c-left > div.mod.pattern-fill-container-mod.chapter-list.mod11 > div > ul > li");
        List<Chapter> chapterList = new ArrayList<>();
        for(Element e : elements){
            String chapterName = e.attr("title");
            String chapterUrl = e.attr("href");
            chapterList.add(new Chapter(chapterName, chapterUrl));
        }
        return chapterList;
    }

    @Override
    public List<String> getContent(String url) {
        return null;
    }

    @Override
    public String getSiteName() {
        return "冰火中文";
    }

    /**
     * 获取图片的完整web地址
     * @param url 书籍地址
     * @return 图片的完整web地址
     */
    private String getImageUrl(String url){
        String html = HttpUtil.getWebResponse(url, getEncoding());
        String imageUrl = Jsoup.parse(html).select("body > div:nth-child(2) > div > div > "
                + "div.c-right > div.sidebar-cover.sidebar-first-region > div > div > div > a > img")
                .attr("src");
        return root+imageUrl;
    }

    private String getIntro(String url){
        String html = HttpUtil.getWebResponse(url, getEncoding());
        return Jsoup.parse(html).select("body > div:nth-child(2) > div > " +
                "div > div.c-right > div.sidebar-cover.sidebar-first-region > div > div > p.intro")
                .text();
    }
}
