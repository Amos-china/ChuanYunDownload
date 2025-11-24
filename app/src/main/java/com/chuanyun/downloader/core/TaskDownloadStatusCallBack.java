package com.chuanyun.downloader.core;


import com.chuanyun.downloader.models.TorrentFileInfoModel;

public interface TaskDownloadStatusCallBack {
    void taskStatus(TorrentFileInfoModel infoModel);
}
