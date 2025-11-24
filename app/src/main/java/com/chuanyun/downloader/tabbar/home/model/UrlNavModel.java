package com.chuanyun.downloader.tabbar.home.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "url_nav_info")
public class UrlNavModel implements Serializable {

    public static final String URL_NAV_MODEL_INTENT = "URL_NAV_MODEL_INTENT";

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;

    @ColumnInfo(name = "tag_name")
    private String tagName;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "create_time",defaultValue = "0")
    private long createTime;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
