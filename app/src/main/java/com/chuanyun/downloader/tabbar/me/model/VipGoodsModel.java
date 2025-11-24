package com.chuanyun.downloader.tabbar.me.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class VipGoodsModel implements MultiItemEntity {

    public static final int MODEL_TYPE_HEADER = 0;
    public static final int MODEL_TYPE_GOODS = 1;
    public static final int MODEL_TYPE_TITLE = 2;
    public static final int MODEL_TYPE_ITEM = 3;
    public static final int MODEL_TYPE_INV = 4;

    private String id;
    private String name;
    private String type;
    private String money;
    private String blurb;
    private boolean select;
    private String vipTime;
    private int imageRes;
    private String subTitle;
    private String yuanJia;
    private int textColor;

    private int itemType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void setVipTime(String vipTime) {
        this.vipTime = vipTime;
    }

    public String getVipTime() {
        return vipTime;
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setYuanJia(String yuanJia) {
        this.yuanJia = yuanJia;
    }

    public String getYuanJia() {
        return yuanJia;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
