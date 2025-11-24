package com.chuanyun.downloader.core;

import android.os.Handler;

import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;
import com.chuanyun.downloader.utils.FileOperate;

public class IAddMagnetTaskHandler implements Runnable {
    private Handler handler;
    private String torrentPath;
    private long taskId;
    private int timeout = 0;
    private String magnet;
    private IAddMagnetTaskListener addMagnetTaskListener;
    //

    public IAddMagnetTaskHandler(Handler handler, long j, String str, String str2, IAddMagnetTaskListener addMagnetTaskListener) {
        this.handler = handler;
        this.taskId = j;
        this.magnet = str;
        this.torrentPath = str2;
        this.addMagnetTaskListener = addMagnetTaskListener;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.timeout += 1000;
        XLTaskInfo taskInfo = XLTaskHelper.getInstance().getTaskInfo(this.taskId);
        if (taskInfo.mTaskStatus == 3) {
            this.addMagnetTaskListener.failed(this.taskId, taskInfo.mErrorCode);
            XLTaskHelper.getInstance().stopTask(this.taskId);
        } else if (taskInfo.mTaskStatus == 0 || taskInfo.mTaskStatus == 1) {
            if (this.timeout >= 8000) {
                this.addMagnetTaskListener.failed(this.taskId, taskInfo.mErrorCode);
                XLTaskHelper.getInstance().stopTask(this.taskId);
                return;
            }
            this.handler.postDelayed(this, 1000L);
        } else if (taskInfo.mTaskStatus == 2 || FileOperate.isExistFile(this.torrentPath)) {
            this.addMagnetTaskListener.succeed(this.taskId, this.torrentPath);
            XLTaskHelper.getInstance().stopTask(this.taskId);
        } else {
            this.addMagnetTaskListener.failed(this.taskId, taskInfo.mErrorCode);
            XLTaskHelper.getInstance().stopTask(this.taskId);
        }
    }
}
