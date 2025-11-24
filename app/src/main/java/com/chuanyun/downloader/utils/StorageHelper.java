package com.chuanyun.downloader.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;

import com.chuanyun.downloader.R;

import java.io.File;

public class StorageHelper {
    public static File createFolder(String name) {
        // 权限已授予，创建文件夹
        File externalDir = new File(Environment.getExternalStorageDirectory(), "穿云下载");
        File folder = new File(externalDir, name);
        if (!folder.exists()) {
            boolean isCreated = folder.mkdirs();
            if (isCreated) {
                return folder;
            } else {
                return null;
            }
        } else {
            return folder;
        }
    }

    public static boolean checkWritStoragePermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public static boolean doesPathExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }


    public static String createTorrentDir() {
        return createFolder("Torrent").toString() + "/";
    }

    public static String createDownloadDir() {
        return createFolder("Download").toString() + "/";
    }

    public static String createDataBaseDir() {return createFolder("DataBase").toString() + "/";}

    public static String createTempDir() {
        File tempFile = createFolder("Temp");
        if (tempFile != null) {
            return createFolder("Temp").toString() + "/";
        }
        return "/sdcard/穿云下载/Temp/";
    }

    public static boolean createDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdir();
        }
        return false;
    }

    public static String getFilePathFromContentUri(Context context,Uri contentUri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        try (Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
