package com.chuanyun.downloader.httpService;

import android.content.Context;

public class BaseEngine {
    protected Context mContext;
    protected HttpRequestInterface request;

    public BaseEngine(Context context) {
        this.mContext = context;
        request = RetrofitHttpRequest.get(context).create(HttpRequestInterface.class);
    }
}
