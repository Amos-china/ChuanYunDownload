package com.chuanyun.downloader.core;

public interface IAddMagnetTaskListener {
    void succeed(long j, String str);

    void failed(long j, int i);
}
