package com.chuanyun.downloader.tabbar.tiktok;

import android.content.Context;

import com.chuanyun.downloader.douyin.VideoInfo;
import com.chuanyun.downloader.httpService.BaseEngine;
import com.chuanyun.downloader.models.ApiRootModel;

import io.reactivex.Observable;

public class TikTokEngine extends BaseEngine {
    public TikTokEngine(Context context) {
        super(context);
    }

    public Observable<ApiRootModel<TikTokRandomModel>> requestRandom(int limit) {
        return request.requestVideoRandom(limit);
    }

    public Observable<ApiRootModel<TikTokRandomModel>> requestCategory(int sort) {
        return request.requestVideoCategory(sort,5);
    }

    public Observable<VideoInfo> requestDouYin(String url) {
        return request.requestDouYin(url);
    }
}
