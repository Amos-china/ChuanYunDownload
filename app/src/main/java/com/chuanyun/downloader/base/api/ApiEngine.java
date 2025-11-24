package com.chuanyun.downloader.base.api;

import android.content.Context;

import com.chuanyun.downloader.httpService.BaseEngine;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.models.ApiRootModel;
import com.chuanyun.downloader.models.RecommendRootModel;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ApiEngine extends BaseEngine {
    public ApiEngine(Context context) {
        super(context);
    }

    public Observable<ApiRootModel<ApiIndexModel>> getApiIndex() {
        return request.getApiIndex().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ApiRootModel<RecommendRootModel>> getRecommendList() {
        return request.getRecommendList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
