package com.example.simplereader.bookshelf;

public class WebBook extends BaseBook {
    private String url;
    private String image;
    private String source;

    public WebBook(String name, String url, String image, String source){
        super(name);
        this.url = url;
        this.image = image;
        this.source = source;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
