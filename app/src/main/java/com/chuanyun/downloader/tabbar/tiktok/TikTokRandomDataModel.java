package com.chuanyun.downloader.tabbar.tiktok;

import com.alibaba.fastjson.annotation.JSONField;

public class TikTokRandomDataModel {
    @JSONField(name = "aweme_id")
    private String awemeId;
    @JSONField(name = "user_id")
    private long userId;
    @JSONField(name = "sec_uid")
    private String secUid;
    @JSONField(name = "video_desc")
    private String videoDesc;
    private int sort;
    private String url;
    private String cover;
    private boolean canPlay;

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }

    public boolean isCanPlay() {
        return canPlay;
    }

    public String getAwemeId() {
        return awemeId;
    }

    public void setAwemeId(String awemeId) {
        this.awemeId = awemeId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSecUid() {
        return secUid;
    }

    public void setSecUid(String secUid) {
        this.secUid = secUid;
    }

    public String getVideoDesc() {
        return videoDesc;
    }

    public void setVideoDesc(String videoDesc) {
        this.videoDesc = videoDesc;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
