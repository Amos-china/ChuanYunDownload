package com.chuanyun.downloader.httpService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpClientUtil {
    private static OkHttpClient defaultClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(new HttpCookieJar())
                .addInterceptor(new HttpInterceptor())
                .connectTimeout(HttpConfig.TIMEOUT, TimeUnit.MILLISECONDS)//设置读取超时时间
                .readTimeout(HttpConfig.TIMEOUT, TimeUnit.MILLISECONDS)//设置请求超时时间
                .writeTimeout(HttpConfig.TIMEOUT, TimeUnit.MILLISECONDS)//设置写入超时时间
                .retryOnConnectionFailure(true);//设置出现错误进行重新连接。
        return builder.build();
    }

    public static OkHttpClient setClient(OkHttpClient client) {
        if (client == null) {
            client = defaultClient();
        }
        return client;
    }

}
