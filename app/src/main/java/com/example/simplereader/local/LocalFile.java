package com.example.simplereader.local;

/**
 * 本地文件属性类
 */
public class LocalFile {

    private String name;

    private String info;

    private String path;

    public LocalFile(String name, String info, String path){
        this.name = name;
        this.info = info;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPath() {
        return path;
    }

}
