package com.chuanyun.downloader.core;

import com.chuanyun.downloader.models.TorrentFileInfoModel;

public interface IDownloadSuccessListener {
    void downloadSuccess(TorrentFileInfoModel fileInfoModel);
}
