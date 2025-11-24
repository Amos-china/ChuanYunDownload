package com.chuanyun.downloader.models;

public class MoreModel {
    private int sec;
    private String title;

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getSec() {
        return sec;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public MoreModel(String title,int sec) {
        this.title = title;
        this.sec = sec;
    }
}
