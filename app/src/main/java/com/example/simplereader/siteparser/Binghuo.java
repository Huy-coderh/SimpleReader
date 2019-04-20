package com.example.simplereader.siteparser;


import com.example.simplereader.sitebean.Book;
import com.example.simplereader.sitebean.BookMsg;
import com.example.simplereader.sitebean.Chapter;
import com.example.simplereader.sitebean.UpdateMsg;
import com.example.simplereader.util.HttpUtil;
import com.example.simplereader.util.StateCallBack;
import com.example.simplereader.util.Utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class Binghuo extends WebSite {

    private final String root = "https://www.bhzw.cc";

    @Override
    public void search(String bookName, StateCallBack<Book> callBack){
        int maxSize = 5;
        String href = "https://www.bhzw.cc/modules/article/search.php";
        RequestBody requestBody = null;
        try {
            requestBody = new FormBody.Builder()
                    .addEncoded("searchkey", URLEncoder.encode(bookName, getEncoding()))
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String html = HttpUtil.getWebHtml(href, getEncoding(), requestBody);
        if(html != null){
            Document doc = Jsoup.parse(html);
            List<Element> elements = doc.select("body > div.wrap > div > div.container-bd > " +
                    "div.mod.mod-clean.pattern-update-list > div > table > tbody > tr");
            int count = 0;
            for(Element e : elements){
                if(count >= maxSize) break;
                String name = e.getElementsByClass("name").first().text();
                String url = root + e.getElementsByClass("name").first().attr("href");
                String image = getImageUrl(url);
                String type = e.getElementsByClass("tag").first().text();
                String author = e.getElementsByClass("author").first().text();
                String intro = getIntro(url);
                String time = e.getElementsByClass("time").first().text();
                callBack.onSuccess(new Book(name, url, author, image, intro, type, time, getSiteName(), getEncoding(),
                        Utility.getMatchValue(bookName, name, author)));
                count++;
            }
        }
    }

    @Override
    public void getCatalog(String url, StateCallBack<List<Chapter>> callBack) {
        String html = HttpUtil.getWebHtml(url, getEncoding());
        if(html != null){
            Document doc = Jsoup.parse(html);
            List<Element> elements = doc.select("body > div:nth-child(2) > div > div > " +
                    "div.c-left > div.mod.pattern-fill-container-mod.chapter-list.mod11 > div > ul > li");
            List<Chapter> chapterList = new ArrayList<>(elements.size());
            for(Element e : elements){
                String chapterName = e.getElementsByTag("a").first().attr("title");
                String chapterUrl = url + e.getElementsByTag("a").first().attr("href");
                chapterList.add(new Chapter(chapterName, chapterUrl));
            }
            callBack.onSuccess(chapterList);
        } else {
            callBack.onFailed("获取目录失败");
        }
    }

    @Override
    public void getContent(String url, StateCallBack<String> callBack) {
        String html = HttpUtil.getWebHtml(url, getEncoding());
        if(html != null){
            html = html.replaceAll("<br />", "\\$");
            Document doc = Jsoup.parse(html);
            String content = doc.select("#ChapterContents").text();
            callBack.onSuccess(content);
        } else {
            callBack.onFailed("获取章节内容失败");
        }
    }

    @Override
    public void getBookInfo(String url, StateCallBack<UpdateMsg> callBack) {
        String html = HttpUtil.getWebHtml(url, getEncoding());
        if(html != null){
            Document doc = Jsoup.parse(html);

            Element element = doc.select("body > div:nth-child(2) > div > div > " +
                    "div.c-left > div.page-header").first();
            String name = element.getElementsByTag("h1").text();
            String author = doc.getElementsByTag("h3").text();
            if(author.length() > 2) author = author.substring(1);
            element = doc.select("body > div:nth-child(2) > div > div > div.c-right > " +
                    "div.sidebar-cover.sidebar-first-region > div > div").first();
            String img = root + element.select("div > a > img").attr("src");

            String intro = element.select("p.intro").text();
            String temp = element.select("p.intro > span").text();
            if(intro.length() > temp.length())
                intro = intro.substring(temp.length());

            String time = element.getElementsByClass("name").last().text();
            temp = element.getElementsByClass("name").last().getElementsByTag("font").first().text();
            if(time.length() > temp.length()) time = time.substring(temp.length());
            temp = element.getElementsByClass("name").last().getElementsByTag("a").first().text();
            if(time.length() > temp.length()) time = time.substring(temp.length());
            if(time.length() > 3) time = time.split("（")[1];
            time = time.substring(0, time.length()-1);

            Book book = new Book(name, url, author, img, intro, null, time, getSiteName(), getEncoding());

            List<Element> elements = doc.select("body > div:nth-child(2) > div >" +
                    " div > div.c-left > div.mod.pattern-fill-container-mod.chapter-list.mod11 >" +
                    " div > ul > li");
            elements = elements.subList(elements.size()>=6?elements.size()-6:0, elements.size());
            List<Chapter> chapters = new ArrayList<>();
            for(Element e : elements){
                chapters.add(new Chapter(e.getElementsByTag("a").text(),
                        root + e.getElementsByTag("a").attr("href")));
            }

            callBack.onSuccess(new BookMsg(book, chapters));
        } else {
            callBack.onFailed("获取书籍信息失败");
        }
    }

    @Override
    public void getUpdateInfo(String url, StateCallBack<UpdateMsg> callBack) {
        String html = HttpUtil.getWebHtml(url, getEncoding());
        if(html != null){
            Document doc = Jsoup.parse(html);
            List<Element> elements = doc.select("body > div:nth-child(2) > div >" +
                    " div > div.c-left > div.mod.pattern-fill-container-mod.chapter-list.mod11 >" +
                    " div > ul > li");
            elements = elements.subList(elements.size()>=6?elements.size()-6:0, elements.size());
            List<Chapter> chapters = new ArrayList<>();
            for(Element e : elements){
                chapters.add(new Chapter(e.getElementsByTag("a").text(),
                        root + e.getElementsByTag("a").attr("href")));
            }
            callBack.onSuccess(new UpdateMsg(chapters));
        } else {
            callBack.onFailed("获取书籍信息失败");
        }
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
        String html = HttpUtil.getWebHtml(url, getEncoding());
        if(html == null) return null;
        String imageUrl = Jsoup.parse(html).select("body > div:nth-child(2) > div > div > "
                + "div.c-right > div.sidebar-cover.sidebar-first-region > div > div > div > a > img")
                .attr("src");
        return root+imageUrl;
    }

    private String getIntro(String url){
        String html = HttpUtil.getWebHtml(url, getEncoding());
        if(html == null) return null;
        Document doc = Jsoup.parse(html);
        String temp = doc.select("body > div:nth-child(2) > div > div > div.c-right > " +
                "div.sidebar-cover.sidebar-first-region > div > div > p.intro > span").text();
        String intro = Jsoup.parse(html).select("body > div:nth-child(2) > div > " +
                "div > div.c-right > div.sidebar-cover.sidebar-first-region > div > div > p.intro")
                .text();
        if(intro.length() > temp.length())
            intro = intro.substring(temp.length());
        return intro;
    }
}
