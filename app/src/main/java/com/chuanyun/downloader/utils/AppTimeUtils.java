package com.chuanyun.downloader.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppTimeUtils {
    public static String convertTimestampToDate(long timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date(timestamp * 1000L); // 时间戳通常是秒，需要乘以1000转为毫秒
        return sdf.format(date);
    }
}
