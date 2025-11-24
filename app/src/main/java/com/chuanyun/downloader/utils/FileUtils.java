package com.chuanyun.downloader.utils;


import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;

public class FileUtils {
    public static String getAvailableSize(String str) {
        StatFs statFs = new StatFs(str);
        statFs.restat(str);
        return String.valueOf(statFs.getAvailableBlocks() * statFs.getBlockSize());
    }

    public static long getAvailableSizes(String str) {
        StatFs statFs = new StatFs(str);
        statFs.restat(str);
        return statFs.getAvailableBlocks() * 1 * statFs.getBlockSize();
    }

    public static long getTotalSize(String str) {
        StatFs statFs = new StatFs(str);
        statFs.restat(str);
        return statFs.getBlockCount() * 1 * statFs.getBlockSize();
    }

    public static String getSDAvailableSize() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return getAvailableSize(Environment.getExternalStorageDirectory().toString());
        }
        return null;
    }

    public static String getSystemAvailableSize() {
        return getAvailableSize("/data");
    }

    public static boolean hasEnoughMemory(String str) {
        long length = new File(str).length();
        return (str.startsWith("/sdcard") || str.startsWith("/mnt/sdcard")) ? ((long) Integer.parseInt(getSDAvailableSize())) > length : ((long) Integer.parseInt(getSystemAvailableSize())) > length;
    }

    public static String getSDTotalSize() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return String.valueOf(getTotalSize(Environment.getExternalStorageDirectory().toString()));
        }
        return null;
    }

    public static String getSysTotalSize() {
        return String.valueOf(getTotalSize("/data"));
    }

    public static String getFormatSize(long j) {
        double j2 = j / 1024.0;
        if (j2 < 1) {
            return j + "B";
        }
        double j3 = j2 / 1024.0;
        if (j3 < 1) {
            return new BigDecimal(Double.toString(j2)).setScale(1, 4).toPlainString() + "K";
        }
        double j4 = j3 / 1024.0;
        if (j4 < 1) {
            return new BigDecimal(Double.toString(j3)).setScale(2, 4).toPlainString() + "M";
        }
        double j5 = j4 / 1024.0;
        if (j5 < 1) {
            return new BigDecimal(Double.toString(j4)).setScale(2, 4).toPlainString() + "G";
        }
        return new BigDecimal(j5).setScale(2, 4).toPlainString() + "T";
    }

    public static String encodeFilePath(String str) {
        return TextUtils.isEmpty(str) ? "" : str.replace(" ", "%20").replace("+", "%2B").replace("?", "%3F").replace("&", "%26").replace("=", "%3D").replace("#", "%23");
    }

    public static String getPrivateDirPath(Context context) {
        return getPrivateDir(context).getPath();
    }

    public static File getPrivateDir(Context context) {
        return context.getFilesDir();
    }


    public static boolean doesFileExist(String path) {
        return new File(path).exists();
    }



    public static boolean deleteFolder(String path) {
        File folder = new File(path);
        return deleteFolder(folder);
    }

    private static boolean deleteFolder(File folder) {
        if (folder != null && folder.isDirectory()) {
            // 获取文件夹下的所有文件和子文件夹
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 如果是子文件夹，递归删除
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        // 删除文件
                        file.delete();
                    }
                }
            }
        }
        // 删除空文件夹
        return folder != null && folder.delete();
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }
}
