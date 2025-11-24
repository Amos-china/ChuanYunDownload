package com.chuanyun.downloader.tabbar.me.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class VipPayModel implements Serializable {

    public static final String INTENT_KEY = "INTENT_VIP_PAY_MODEL";

    private String name;
    @JSONField(name = "order_no")
    private String orderNo;
    @JSONField(name = "trade_no")
    private String tradeNo;
    private String money;
    private int state;
    private long addTime;
    private long expTime;
    private String payUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getExpTime() {
        return expTime;
    }

    public void setExpTime(long expTime) {
        this.expTime = expTime;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
}
