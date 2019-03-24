package com.example.simplereader.util;

import java.io.File;

public class SeekFile {

    private File file;

    public SeekFile(File file){
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public boolean isDirectory(){
        if(file.isDirectory()){
            return true;
        }
        return false;
    }

    public boolean isTxt(){
        if(!file.isDirectory()){
            String str = file.getName();
            int n = str.length();
            //if(file.getName().lastIndexOf(".txt")>=0)
            if(str.substring(n-4, n).equals(".txt")) {
                return true;
            }
        }
        return false;
    }

}
