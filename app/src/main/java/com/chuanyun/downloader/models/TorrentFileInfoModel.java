package com.chuanyun.downloader.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.alibaba.fastjson.annotation.JSONField;
import com.xunlei.downloadlib.XLTaskHelper;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.core.TaskDownloadStatusCallBack;
import com.chuanyun.downloader.dao.DownloadDao;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

@Entity(tableName = "download_info")
public class TorrentFileInfoModel implements Serializable {

    public static final String INTENT_FILE_INFO_MODEL = "INTENT_FILE_INFO_MODEL";

    public static final int DOWNLOAD_STATUS_STOP = 0;
    public static final int DOWNLOAD_STATUS_DOWNLOADING = 1;
    public static final int DOWNLOAD_STATUS_FINISH = 2;
    public static final int DOWNLOAD_STATUS_FAIL = 3;
    public static final int DOWNLOAD_STATUS_WAIT = 4;
    public static final int DOWNLOAD_STATUS_ERROR = 5;

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "info_id")
    @JSONField(name = "info_id")
    private String infoId;

    @ColumnInfo(name = "index")
    @JSONField(name = "mFileIndex")
    private int index;

    @ColumnInfo(name = "name")
    @JSONField(name = "mFileName")
    private String name;

    @ColumnInfo(name = "size")
    @JSONField(name = "mFileSize")
    private long size;

    @ColumnInfo(name = "real_index")
    @JSONField(name = "mRealIndex")
    private int realIndex;

    @ColumnInfo(name = "sub_path")
    @JSONField(name = "mSubPath")
    private String subPath;


    @ColumnInfo(name = "download_status")
    private int downloadStatus;

    ////0：未知  1：视频 2：图片 3.音频文件 4.安装包 5：压缩文件 6.文本文件 7.url
    @ColumnInfo(name = "file_suffix_type")
    private int fileSuffixType;

    //=======

    @ColumnInfo(name = "file_magnet_type")
    private int fileMagnetType;

    @ColumnInfo(name = "task_id")
    private long taskId;

    @ColumnInfo(name = "torrent_name")
    private String torrentName;

    @ColumnInfo(name = "file_path")
    private String filePath;

    @ColumnInfo(name = "hash")
    @JSONField(name = "hash")
    private String hash;

    @ColumnInfo(name = "torrent_path")
    @JSONField(name = "torrent_path")
    private String torrentPath;

    @ColumnInfo(name = "magnet")
    @JSONField(name = "magnet")
    private String magnet;

    @ColumnInfo(name = "create_time")
    @JSONField(name = "create_time")
    private long createTime;

    @ColumnInfo(name = "has_sub_path")
    private boolean hasSubPath;

    @ColumnInfo(name = "download_size")
    private long downloadSize;

    @ColumnInfo(name = "speed")
    private long speed;

    @Ignore
    private boolean select;

    @ColumnInfo(name = "is_download")
    private boolean isDownload;

    @Ignore
    private int retryCount = 0;

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public boolean isDownload() {
        return isDownload;
    }

    @NonNull
    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(@NonNull String infoId) {
        this.infoId = infoId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getRealIndex() {
        return realIndex;
    }

    public void setRealIndex(int realIndex) {
        this.realIndex = realIndex;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTorrentPath() {
        return torrentPath;
    }

    public void setTorrentPath(String torrentPath) {
        this.torrentPath = torrentPath;
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

    public boolean isHasSubPath() {
        return hasSubPath;
    }

    public void setHasSubPath(boolean hasSubPath) {
        this.hasSubPath = hasSubPath;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getTorrentName() {
        return torrentName;
    }

    public void setTorrentName(String torrentName) {
        this.torrentName = torrentName;
    }


    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public long getSpeed() {
        return speed;
    }


    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setFileSuffixType(int fileSuffixType) {
        this.fileSuffixType = fileSuffixType;
    }

    public int getFileSuffixType() {
        return fileSuffixType;
    }

    public void setFileMagnetType(int fileMagnetType) {
        this.fileMagnetType = fileMagnetType;
    }

    public int getFileMagnetType() {
        return fileMagnetType;
    }

    @Ignore
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    @Ignore
    private TaskDownloadStatusCallBack taskDownloadStatusCallBack;

    public void setTaskDownloadStatusCallBack(TaskDownloadStatusCallBack taskDownloadStatusCallBack) {
        this.taskDownloadStatusCallBack = taskDownloadStatusCallBack;
    }


    @Ignore
    private String videoPath;

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }


    @Ignore
    private Disposable disposable;

    public void start() {
        disposable = Observable.just("")
                .map(path -> {
                    long taskId;
                    if (getFileMagnetType() == 1) {
                        taskId = XLTaskHelper.getInstance().addTorrentTask(getTorrentPath(),videoPath,new int[]{getIndex()});
                    }else {
                        taskId = XLTaskHelper.getInstance().addThunderTask(getMagnet(),videoPath,getName(),token,0);
                    }
                    setTaskId(taskId);
                    return taskId;
                })
                .flatMap(taskId -> Observable.interval(1, TimeUnit.SECONDS))
                .map(aLong -> XLTaskHelper.getInstance().getTaskInfo(getTaskId()))
                .map(taskInfo -> {

                    if (disposable == null) {
                        return taskInfo;
                    }

                    setDownloadStatus(taskInfo.mTaskStatus);
                    setDownloadSize(taskInfo.mDownloadSize);
                    setSpeed(taskInfo.mDownloadSpeed);

                    DownloadDao downloadDao = App.getApp().getAppDataBase().downloadDao();
                    downloadDao.updateDownloadInfo(taskInfo.mDownloadSize,taskInfo.mTaskStatus,infoId,taskId).subscribe();
                    return taskInfo;
                })
                .retry()
                .subscribe(taskInfo -> {
                    if (taskDownloadStatusCallBack != null) {
                        taskDownloadStatusCallBack.taskStatus(this);
                    }
                },throwable -> {
                    setDownloadStatus(DOWNLOAD_STATUS_FAIL);
                    if (taskDownloadStatusCallBack != null) {
                        taskDownloadStatusCallBack.taskStatus(this);
                    }
                });
    }

    public void stopDownload() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }
}
