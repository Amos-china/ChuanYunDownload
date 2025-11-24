package com.chuanyun.downloader.tabbar.model;

public class TabBarItemModel {
    private String title;
    private int imageRes;
    private int selectImageRes;
    private boolean select;

    public TabBarItemModel(String title, int imageRes, int selectImageRes) {
        this.title = title;
        this.imageRes = imageRes;
        this.selectImageRes = selectImageRes;
    }

    public String getTitle() {
        return title;
    }

    public int getImageRes() {
        return imageRes;
    }


    public void setSelectImageRes(int selectImageRes) {
        this.selectImageRes = selectImageRes;
    }

    public int getSelectImageRes() {
        return selectImageRes;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isSelect() {
        return select;
    }
}
