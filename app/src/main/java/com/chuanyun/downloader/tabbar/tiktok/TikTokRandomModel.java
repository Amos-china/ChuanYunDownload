package com.chuanyun.downloader.tabbar.tiktok;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class TikTokRandomModel {
    private String status;
    private int count;
    @JSONField(name = "data")
    private List<TikTokRandomDataModel> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<TikTokRandomDataModel> getData() {
        return data;
    }

    public void setData(List<TikTokRandomDataModel> data) {
        this.data = data;
    }
}
