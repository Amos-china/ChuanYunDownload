package com.chuanyun.downloader.httpService;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;
import com.chuanyun.downloader.app.App;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class HttpInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();
        builder.addHeader("Content-Type", "application/json; charset=utf-8");

        if (!TextUtils.isEmpty(App.getApp().getRequestApi())) {
            HttpUrl newBaseUrl = HttpUrl.parse(App.getApp().getRequestApi());
            HttpUrl newUrl = originalRequest.url()
                    .newBuilder()
                    .scheme(newBaseUrl.scheme())
                    .host(newBaseUrl.host())
                    .build();

            Request newRequest = originalRequest.newBuilder().url(newUrl).build();
            return chain.proceed(newRequest);
        }

        Request request = builder.build();
        return chain.proceed(request);
    }

    public static void changeApi() {
        MMKV.defaultMMKV().putBoolean("change_api",true);
    }

    public static boolean isApiChanged() {
        return MMKV.defaultMMKV().getBoolean("change_api",false);
    }
}
