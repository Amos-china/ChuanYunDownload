package com.chuanyun.downloader.core;

import com.chuanyun.downloader.models.TorrentFileInfoModel;

public interface IDownloadTaskListener {
    void addDownloadTask(TorrentFileInfoModel fileInfoModel);
    void removeDownloadTask(TorrentFileInfoModel fileInfoModel);
    void reloadList();
    void reloadItem(TorrentFileInfoModel fileInfoModel);
}
