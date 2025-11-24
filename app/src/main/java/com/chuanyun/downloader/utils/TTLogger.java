package com.chuanyun.downloader.utils;

import android.util.Log;

public class TTLogger {
    private static String TAG = "FC_TTLogger";

    public static void i(String str) {
        Log.i(TAG,"-------------------");
        Log.i(TAG,str);
        Log.i(TAG,"-------------------");
    }


}
