package com.chuanyun.downloader.downloadService;

public class FCTaskInfo {
    public int health;
    public long mDownloadSize;
    public long mDownloadSpeed;
    public long mFileSize;
    public long mTaskId;
    public int mTaskStatus;

    public String toString() {
        return "id:" + this.mTaskId + " speed:" + FCHelper.byteFormat(this.mDownloadSpeed, true) + " downSize:" + FCHelper.byteFormat(this.mDownloadSize, true) + "  fileSize:" + FCHelper.byteFormat(this.mFileSize, true) + " status:" + this.mTaskStatus;
    }
}
