package com.chuanyun.downloader.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class RecommendRootModel {
    @JSONField(name = "site")
    private List<RecommendURLModel> siteList;

    @JSONField(name = "recommend")
    private List<RecommendURLModel> recommendList;

    @JSONField(name = "ad")
    private List<RecommendURLModel> adList;

    @JSONField(name = "douyin")
    private List<RecommendURLModel> tiktok;


    public List<RecommendURLModel> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<RecommendURLModel> siteList) {
        this.siteList = siteList;
    }

    public List<RecommendURLModel> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(List<RecommendURLModel> recommendList) {
        this.recommendList = recommendList;
    }

    public List<RecommendURLModel> getAdList() {
        return adList;
    }

    public void setAdList(List<RecommendURLModel> adList) {
        this.adList = adList;
    }

    public void setTiktok(List<RecommendURLModel> tiktok) {
        this.tiktok = tiktok;
    }

    public List<RecommendURLModel> getTiktok() {
        return tiktok;
    }
}
