package com.chuanyun.downloader.tabbar.tiktok;

public class TiktokSortModel {
    private String title;
    private int sort;

    public TiktokSortModel(String title, int sort) {
        this.title = title;
        this.sort = sort;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getSort() {
        return sort;
    }
}
