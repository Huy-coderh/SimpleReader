package com.example.simplereader.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    /**
     * 发送请求
     * @param address 地址
     * @param callback 回调
     */
    public static void sendOkHttpRequest(String address, Callback callback){
        sendOkHttpRequest(address, null, callback);
    }

    /**
     * 发送网络请求
     * @param url
     * @param requestBody
     * @param callback
     */
    public static void sendOkHttpRequest(String url, RequestBody requestBody, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        if(requestBody != null){
            builder.post(requestBody);
        }
        Request request = builder
                .url(url)
                .addHeader("user-agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/72.0.3626.121 Safari/537.36")
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 带有请求体的连接website获取网页内容的方法(同步调用)
     * @param url website地址
     * @param encoding website编码方式
     * @param requestBody 请求体
     * @return website内容(html文件)
     */
    public static String getWebHtml(String url, String encoding, RequestBody requestBody) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        Request.Builder builder = new Request.Builder();
        if(requestBody != null){
            builder.post(requestBody);
        }
        Request request = builder
                .url(url)
                .addHeader("user-agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/72.0.3626.121 Safari/537.36")
                .build();
        String result = null;
        try {
            result = new String(client.newCall(request).execute().body().bytes(), "GBK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 不带请求体的连接website获取网页内容的方法(同步调用)
     * @param url website地址
     * @param encoding website编码方式
     * @return website内容(html文件)
     */
    public static String getWebHtml(String url, String encoding) {
        return getWebHtml(url, encoding, null);
    }

}
