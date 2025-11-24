package com.chuanyun.downloader.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "torrent_info")
public class TTTorrentInfo implements Serializable {

    public static final String INTENT_TTTORRENT_INFO = "INTENT_TTTORRENT_INFO";

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "hash")
    @JSONField(name = "mInfoHash")
    private String hash;

    @ColumnInfo(name = "file_count")
    @JSONField(name = "mFileCount")
    private int fileCount;

    @ColumnInfo(name = "is_multi_files")
    @JSONField(name = "mIsMultiFiles")
    private String isMultiFiles;

    @ColumnInfo(name = "torrent_name")
    @JSONField(name = "mMultiFileBaseFolder")
    private String torrentName;


    //====

    @ColumnInfo(name = "magnet")
    @JSONField(name = "magnet")
    private String magnet;

    @ColumnInfo(name = "create_time")
    @JSONField(name = "create_time")
    private long createTime;

    @ColumnInfo(name = "size")
    @JSONField(name = "size")
    private String size;

    @ColumnInfo(name = "is_download")
    private boolean isDownload;


    @ColumnInfo(name = "is_del")
    @JSONField(name = "is_del")
    private int isDel;

    @ColumnInfo(name = "path")
    @JSONField(name = "path")
    private String path;

    @ColumnInfo(name = "magnet_type")
    private int magnetType;

    @ColumnInfo(name = "is_like")
    @JSONField(name = "is_like")
    private int isLike;

    @Ignore
    private boolean isSelect;

    @Ignore
    @JSONField(name = "mSubFileInfo")
    private List<TorrentFileInfoModel> fileModelList;

    @Ignore
    private int currentPlayIndex;


    public void setHash(@NonNull String hash) {
        this.hash = hash;
    }

    @NonNull
    public String getHash() {
        return hash;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTorrentName() {
        return torrentName;
    }

    public void setTorrentName(String torrentName) {
        this.torrentName = torrentName;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }


    public void setFileModelList(List<TorrentFileInfoModel> fileModelList) {
        this.fileModelList = fileModelList;
    }

    public List<TorrentFileInfoModel> getFileModelList() {
        return fileModelList;
    }


    public void setIsMultiFiles(String isMultiFiles) {
        this.isMultiFiles = isMultiFiles;
    }

    public String getIsMultiFiles() {
        return isMultiFiles;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void setCurrentPlayIndex(int currentPlayIndex) {
        this.currentPlayIndex = currentPlayIndex;
    }

    public int getCurrentPlayIndex() {
        return currentPlayIndex;
    }

    public void setMagnetType(int magnetType) {
        this.magnetType = magnetType;
    }

    public int getMagnetType() {
        return magnetType;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getIsLike() {
        return isLike;
    }
}
