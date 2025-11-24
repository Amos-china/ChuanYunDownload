package com.chuanyun.downloader.app;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.e4a.runtime.上下文操作;
import com.tencent.mmkv.MMKV;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushRegisterCallback;
import com.chuanyun.downloader.config.Constant;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.utils.MMKVUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BaseApp extends Application {
    private ApiIndexModel apiIndexModel;
    private static final String channel = "1";

    private String deviceUuid;

    private long cloudTime;

    private String requestApi;

    public void setRequestApi(String requestApi) {
        this.requestApi = requestApi;
    }

    public String getRequestApi() {
        return requestApi;
    }

    public void setCloudTime(long cloudTime) {
        this.cloudTime = cloudTime;
    }

    public long getCloudTime() {
        if (cloudTime == 0) {
            return System.currentTimeMillis() / 1000;
        }
        return cloudTime;
    }

    public String getDeviceUuid() {
        if (TextUtils.isEmpty(deviceUuid)) {
            return getAndroidId();
        }
        return deviceUuid;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        checkADB();




        MMKV.initialize(this);

        上下文操作.置全局上下文(this);

        getAndroidOaid();
    }

    public void setApiIndexModel(ApiIndexModel apiIndexModel) {
        this.apiIndexModel = apiIndexModel;
    }

    public ApiIndexModel getApiIndexModel() {
        return apiIndexModel;
    }


    private void abc() {
     Disposable disposable = Observable.interval(5,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(aLong -> {
                    if (DeviceUtils.isDeviceRooted() || DeviceUtils.isAdbEnabled()) {
                        return true;
                    }
                    return false;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        AppUtils.exitApp();
                    }
                }, Throwable::printStackTrace);
    }

    private String getAndroidId() {
        return DeviceUtils.getAndroidID();
    }

    private void getAndroidOaid() {
        if (DeviceUtils.isEmulator()) {
            deviceUuid = getAndroidId();
        }
        UMConfigure.getOaid(this,uuid -> {
            if (!TextUtils.isEmpty(uuid)) {
                if (uuid.contains("0000")) {
                    deviceUuid = getAndroidId();
                    Log.i("TAG", "getAndroidid000000: " + deviceUuid);
                }else {
                    deviceUuid = uuid;
                    Log.i("TAG", "getAndroidOaid: " + deviceUuid);
                }
            }else {
                deviceUuid = getAndroidId();
                Log.i("TAG", "getAndroidid: " + deviceUuid);
            }
        });
    }

    private void checkADB() {
        Disposable disposable = Observable.interval(1,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(aLong -> DeviceUtils.isDevelopmentSettingsEnabled() && DeviceUtils.isAdbEnabled())
                .filter(aBoolean -> aBoolean)
                .subscribe(aBoolean -> {
                    System.exit(0);
                });
    }
}
