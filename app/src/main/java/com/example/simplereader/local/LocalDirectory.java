package com.example.simplereader.local;

import com.example.simplereader.R;

/**
 * 本地文件夹属性类
 */
public class LocalDirectory extends LocalFile {

    private int imageId;

    public LocalDirectory(String name, String info, String path){
        super(name, info, path);
        this.imageId = R.drawable.ic_file;
    }

    public int getImageId() {
        return imageId;
    }
}
