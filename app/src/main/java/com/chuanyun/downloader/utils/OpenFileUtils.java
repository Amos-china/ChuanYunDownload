package com.chuanyun.downloader.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class OpenFileUtils {
    public static void openFile(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取文件的 MIME 类型
        String mimeType = getMimeType(filePath);
        if (mimeType == null) {
            mimeType = "*/*"; // 如果无法获取 MIME 类型，使用通配符
        }

        // 创建 Intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri fileUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0 及以上版本需要使用 FileProvider
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileUri = Uri.fromFile(file);
        }

        intent.setDataAndType(fileUri, mimeType);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "未找到可以打开该文件的应用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据文件路径获取对应的 MIME 类型
     *
     * @param filePath 文件路径
     * @return MIME 类型
     */
    public static String getMimeType(String filePath) {
        if (filePath == null) {
            return "*/*";
        }

        // 提取文件扩展名（包括点号 ".ext"）
        String extension = getFileExtension(filePath);

        if (extension.isEmpty()) {
            return "*/*";
        }

        // 遍历 MIME_MapTable 查找匹配项
        for (String[] map : MIME_MapTable) {
            if (extension.equalsIgnoreCase(map[0])) {
                return map[1];
            }
        }

        // 未找到匹配类型时返回默认值
        return "*/*";
    }

    /**
     * 获取文件扩展名
     *
     * @param filePath 文件路径
     * @return 文件扩展名（带点号），如 ".jpg"
     */
    private static String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < filePath.length() - 1) {
            return filePath.substring(lastDot).toLowerCase();
        }
        return ""; // 无扩展名
    }

    //建立一个MIME类型与文件后缀名的匹配表
    private static final String[][] MIME_MapTable={
            //{后缀名，    MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };
}
