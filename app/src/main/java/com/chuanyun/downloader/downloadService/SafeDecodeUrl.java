package com.chuanyun.downloader.downloadService;

import android.text.TextUtils;

import java.net.URLDecoder;

public class SafeDecodeUrl {
    public static String decode(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }
}
