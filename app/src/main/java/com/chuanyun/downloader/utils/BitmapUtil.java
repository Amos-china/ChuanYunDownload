package com.chuanyun.downloader.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class BitmapUtil {

    public static Bitmap base64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

        // 使用 BitmapFactory.Options 来设置图片的缩放
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  // 只获取图片的宽高
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);

        // 获取图片的缩放比例
        int scale = 1;
        int width = options.outWidth;
        int height = options.outHeight;
        int reqWidth = 100;  // 设置你想要的宽度
        int reqHeight = 100;  // 设置你想要的高度

        if (width > reqWidth || height > reqHeight) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            scale = Math.max(heightRatio, widthRatio);
        }

        // 使用 scale 来压缩图片
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);
    }

    // Bitmap 转 Base64 字符串
    public static String bitmapToBase64(Bitmap bitmap) {
        // 创建一个字节数组输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 将 Bitmap 压缩为字节流，选择压缩格式（JPEG/PNG）及质量
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        // 获取字节数组
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // 将字节数组转换为 Base64 字符串
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
