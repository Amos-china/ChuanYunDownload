package com.chuanyun.downloader.core;

import com.chuanyun.downloader.models.TTTorrentInfo;

public interface IOpenTorrentListener {
    void onSuccess(TTTorrentInfo ttTorrentInfo);
    void fail(int code);
}
