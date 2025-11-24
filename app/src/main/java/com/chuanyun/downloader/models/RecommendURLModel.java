package com.chuanyun.downloader.models;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class RecommendURLModel implements MultiItemEntity, Serializable {

    public static final int MODEL_TYPE_TITLE = 1;
    public static final int MODEL_TYPE_ITEM = 0;
    public static final int MODEL_TYPE_ADD = 2;

    public static final String URL_NAV_MODEL_INTENT = "URL_NAV_MODEL_INTENT";

    private String name;
    private String htmlName;
    private String url;
    private String icon;
    private int id;
    private int itemType;
    private int sort;
    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getSort() {
        return sort;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getItemType() {
        return itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHtmlName() {
        return htmlName;
    }

    public void setHtmlName(String htmlName) {
        this.htmlName = htmlName;
    }
}
