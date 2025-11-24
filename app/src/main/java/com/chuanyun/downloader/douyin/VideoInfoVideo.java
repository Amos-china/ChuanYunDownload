package com.chuanyun.downloader.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class VideoInfoVideo {
    private String url;
    @JSONField(name = "play_url")
    private String playUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }
}
