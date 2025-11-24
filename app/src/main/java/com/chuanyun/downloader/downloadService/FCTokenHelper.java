package com.chuanyun.downloader.downloadService;

import com.flash.download.EncryptHelper;
import com.chuanyun.downloader.app.App;

public class FCTokenHelper {

    private static int version = 0;
    private static String token = "";
    private static String uid = "";

    public static void setVersion(int i) {
        version = i;
    }

    public static void setToken(String str) {
        token = str;
    }

    public static void setUid(String str) {
        uid = str;
    }

    public static String getToken() {
        return EncryptHelper.a(version, token, uid, FCUtils.getPeerid(App.getApp()));
    }
}
