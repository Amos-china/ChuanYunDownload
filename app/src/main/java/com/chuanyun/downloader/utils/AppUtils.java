package com.chuanyun.downloader.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppUtils {
    public static int getVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode; // 获取 versionCode
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1; // 出现异常返回 -1
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return "v" + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "v1.0.0"; // 出现异常返回
    }
}
