package com.chuanyun.downloader.models;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tencent.mmkv.MMKV;

public class AppSettingsModel {
    private int downloadTaskCount = 3;
    private boolean useMobileDownload = true;
    private boolean lowBatteryDownload = true;
    private boolean ringNotice = true;
    private boolean statusBarNotice = true;
    private boolean whiteList = false;

    public int getDownloadTaskCount() {
        return downloadTaskCount;
    }

    public void setDownloadTaskCount(int downloadTaskCount) {
        this.downloadTaskCount = downloadTaskCount;
    }

    public boolean isUseMobileDownload() {
        return useMobileDownload;
    }

    public void setUseMobileDownload(boolean useMobileDownload) {
        this.useMobileDownload = useMobileDownload;
    }

    public boolean isLowBatteryDownload() {
        return lowBatteryDownload;
    }

    public void setLowBatteryDownload(boolean lowBatteryDownload) {
        this.lowBatteryDownload = lowBatteryDownload;
    }

    public boolean isRingNotice() {
        return ringNotice;
    }

    public void setRingNotice(boolean ringNotice) {
        this.ringNotice = ringNotice;
    }

    public boolean isStatusBarNotice() {
        return statusBarNotice;
    }

    public void setStatusBarNotice(boolean statusBarNotice) {
        this.statusBarNotice = statusBarNotice;
    }

    public boolean isWhiteList() {
        return whiteList;
    }

    public void setWhiteList(boolean whiteList) {
        this.whiteList = whiteList;
    }

    private static final String MMKV_SETTINGS = "MMKV_SETTINGS";

    public static void saveSettings(AppSettingsModel settingsModel) {
        String value = JSON.toJSONString(settingsModel);
        MMKV.defaultMMKV().putString(MMKV_SETTINGS,value);
    }

    public static AppSettingsModel getSettingsModel() {
        String value = MMKV.defaultMMKV().getString(MMKV_SETTINGS,"");
        if (TextUtils.isEmpty(value)) {
            return new AppSettingsModel();
        }else {
            return JSON.parseObject(value,AppSettingsModel.class);
        }
    }
}
