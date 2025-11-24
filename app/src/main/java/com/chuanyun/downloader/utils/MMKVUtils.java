package com.chuanyun.downloader.utils;

import com.tencent.mmkv.MMKV;
import com.chuanyun.downloader.config.MMKVConstant;

public class MMKVUtils {
    public static boolean getIsReadPrivacy() {
       return MMKV.defaultMMKV().getBoolean(MMKVConstant.READ_PRIVACY,false);
    }

    public static void setReadPrivacy() {
        MMKV.defaultMMKV().putBoolean(MMKVConstant.READ_PRIVACY,true);
    }
}
