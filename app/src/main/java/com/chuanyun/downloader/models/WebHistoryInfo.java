package com.chuanyun.downloader.models;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "web_history_info")
public class WebHistoryInfo {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "byte_image")
    private String byteImage;

    @ColumnInfo(name = "create_time")
    private long createTime;

    @Ignore
    private Bitmap imageBitmap;

    @ColumnInfo(name = "collect")
    private int collect;

    @ColumnInfo(name = "del")
    private int del;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getByteImage() {
        return byteImage;
    }

    public void setByteImage(String byteImage) {
        this.byteImage = byteImage;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setDel(int del) {
        this.del = del;
    }

    public int getDel() {
        return del;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getCollect() {
        return collect;
    }
}
