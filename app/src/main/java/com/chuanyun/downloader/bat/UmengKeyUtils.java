package com.chuanyun.downloader.bat;

import android.content.Context;
import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UmengKeyUtils {
    private Observable<UmengConfigModel> getUmengConfig() {
        return Observable.create(emitter -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // 连接超时
                    .readTimeout(30, TimeUnit.SECONDS)   // 读取超时
                    .writeTimeout(30, TimeUnit.SECONDS)  // 写入超时
                    .build();

            Request request = new Request.Builder()
                    .url("https://raw.githubusercontent.com/umeng-key/jsonKey/main/key.json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    UmengConfigModel configModel = JSON.parseObject(jsonData,UmengConfigModel.class);
                    Log.i("FUCK", "getUmengConfig:  "  + jsonData);
                    emitter.onNext(configModel);
                } else {
                    UmengConfigModel configModel = new UmengConfigModel();
                    configModel.setId(0);
                    emitter.onNext(configModel);
                }
            } catch (IOException e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        });
    }

    public void config() {
        getUmengConfig().subscribeOn(Schedulers.io())
                .subscribe(configModel -> {
                    if (configModel.getData() != null) {
                        //do exit
                        Log.i("FUCK",JSON.toJSONString(configModel));
                    }
                },throwable -> {
                    Log.i("FUCK", "config: " + throwable.getMessage());
                },()->{

                });
    }

    private static void uploadEvent(Context context,int type, String money) {
        UmengEventData data = new UmengEventData();
        data.setEventType(type);

        Map<String,Object> params = new HashMap<>();

        if (data.getEventType() == 1) {
            data.setEventId("jiexi");
        }else if(data.getEventType() == 2) {
            data.setEventId("xiazai");
        }else {
            data.setEventId("zhifu");
            params.put("money",money);
        }

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime = sdf.format(currentTime);
        params.put("time",formattedTime);

        MobclickAgent.onEventObject(context,data.getEventId(),params);
    }

    public static void uploadPay(Context context,String money) {
        UmengKeyUtils.uploadEvent(context,3, money);
    }

    public static void uploadDownload(Context context,int type) {
        uploadEvent(context,type,"");
    }
}
