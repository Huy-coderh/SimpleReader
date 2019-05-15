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

import javax.security.auth.callback.Callback;

public class Luoqiu extends WebSite {

    private String root = "https://www.luoqiu.cc";

    @Override
    public void search(String bookName, StateCallBack<Book> callBack) {
        int maxSize = 20;
        String href = "";
        try {
            href = "https://www.luoqiu.cc/modules/article/search.php?searchkey=" +URLEncoder.encode(bookName,getEncoding());
            System.out.println(href);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String html = HttpUtil.getWebHtml(href, getEncoding());
        if(html != null){
            Document doc = Jsoup.parse(html);
            List<Element> elements = doc.select("#content > div.inner > div.details").first()
                    .getElementsByClass("item-pic");
            int count = 0;
            //一头一尾读取20个搜索记录
            for(int i=0,j=elements.size()-1; i<=j; i++,j--){
                Element e;
                if(count <= maxSize) {
                    e = elements.get(i);
                    String name = e.select("h3 > a").first().text();
                    String temp = e.select("h3 > a").first().attr("href");
                    temp = ( temp.length()>4?temp.substring(0, temp.length()-3) : "/0" ) + temp;
                    String url = root + temp + "/";
                    String image = root + e.select("a > img").first().attr("src");
                    String type = e.select("p:nth-child(3) > i:nth-child(2)").text();
                    String author = e.select("p:nth-child(3) > i:nth-child(1)").text();
                    String time = e.select("p:nth-child(4) > i").text();
                    String intro = e.select("p:nth-child(5)").text();
                    callBack.onSuccess(new Book(name, url, author, image, intro, type, time, getSiteName(), getEncoding(),
                            Utility.getMatchValue(bookName, name, author)));
                    count ++;
                    if(j != i ){
                        e = elements.get(j);
                        name = e.select("h3 > a").first().text();
                        temp = e.select("h3 > a").first().attr("href");
                        temp = ( temp.length()>4?temp.substring(0, temp.length()-3) : "/0" ) + temp;
                        url = root + temp + "/";
                        image = root + e.select("a > img").first().attr("src");
                        type = e.select("p:nth-child(3) > i:nth-child(2)").text();
                        author = e.select("p:nth-child(3) > i:nth-child(1)").text();
                        time = e.select("p:nth-child(4) > i").text();
                        intro = e.select("p:nth-child(5)").text();
                        callBack.onSuccess(new Book(name, url, author, image, intro, type, time, getSiteName(), getEncoding(),
                                Utility.getMatchValue(bookName, name, author)));
                    }
                }
            }
        }
    }

    @Override
    public void getCatalog(String url, StateCallBack<List<Chapter>> callBack) {
        String html = HttpUtil.getWebHtml(url, getEncoding());
        if(html != null){
            Document doc = Jsoup.parse(html);
            List<Element> elements = doc.select("#main > div > dl > dd");
            if(elements.size() > 18){
                elements = elements.subList(9, elements.size());
            } else {
                elements = elements.subList(elements.size()/2, elements.size());
            }
            List<Chapter> chapters = new ArrayList<>();
            for(Element e : elements){
                String title = e.getElementsByTag("a").first().text();
                String href = root + e.getElementsByTag("a").first().attr("href");
                chapters.add(new Chapter(title, href));
            }
            callBack.onSuccess(chapters);
        } else {
            callBack.onFailed("获取目录失败");
        }
    }

    @Override
    public void getContent(String chapterUrl, StateCallBack<String> callBack) {
        String html = HttpUtil.getWebHtml(chapterUrl, getEncoding());
        if(html != null){
            html = html.replaceAll("<br />", "\\$");
            Document doc = Jsoup.parse(html);
            String content = doc.select("#BookText").first().text();
            content = content.replaceAll("</p>", "");
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
            Element element = doc.select("#container > div.bookinfo").first();
            String name = element.select("div > h1").first().text();
            String author = element.select("div > em > a").text();
            String temp = element.select("p.intro > b").text();
            String intro = element.getElementsByClass("intro").first().text();
            if(intro.length()>temp.length()) intro = intro.substring(temp.length());
            String time = element.getElementsByClass("stats").first()
                    .getElementsByClass("fr").first().getElementsByTag("i").text();
            Book book = new Book(name, url, author, null, intro, null, time, getSiteName(), getEncoding());

            List<Element> elements = doc.select("#main > div > dl > dd");
            //该网站上最新章节最多显示9条且和目录糅合在一起,要把多余的过滤,取最新的六条
            if(elements.size() >= 12) {
                elements = elements.subList(0, 6);
            } else {
                elements = elements.subList(0, elements.size()/2);
            }
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
            List<Element> elements = doc.select("#main > div > dl > dd");
            //该网站上最新章节最多显示9条且和目录糅合在一起,要把多余的过滤,取最新的六条
            if(elements.size() >= 12) {
                elements = elements.subList(0, 6);
            } else {
                elements = elements.subList(0, elements.size()/2);
            }
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
        return "落秋中文";
    }

    @Override
    public String getEncoding() {
        return "GBK";
    }
}
