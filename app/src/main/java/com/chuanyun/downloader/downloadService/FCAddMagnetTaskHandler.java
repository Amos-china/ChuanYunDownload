package com.chuanyun.downloader.downloadService;

import android.os.Handler;

import com.ghost.downengine.FlashDownEngineImpl;
import com.chuanyun.downloader.core.IAddMagnetTaskListener;
import com.chuanyun.downloader.utils.FileOperate;


public class FCAddMagnetTaskHandler implements Runnable{

    private static final String TAG = "AddMagnetTaskHandler";
    private static int mTimeout = 15000;
    private Handler handler;
    private String torrentPath;
    private long taskId;
    private int timeout = 0;
    private String magnet;
    private IAddMagnetTaskListener addMagnetTaskListener;

    public FCAddMagnetTaskHandler(android.os.Handler handler, long j, String str, String str2, IAddMagnetTaskListener iAddMagnetTaskListener) {
        this.handler = handler;
        this.taskId = j;
        this.magnet = str;
        this.torrentPath = str2;
        this.addMagnetTaskListener = iAddMagnetTaskListener;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.timeout += 1000;
        FlashDownEngineImpl.getTaskInfo((int) this.taskId);
        FCTaskInfo taskInfo = FCHelper.getTaskInfo(this.taskId);
        if (taskInfo.mTaskStatus == 2 && FileOperate.isExistFile(this.torrentPath)) {
            this.addMagnetTaskListener.succeed(this.taskId, this.torrentPath);
            FlashDownEngineImpl.stopTask((int) this.taskId);
        } else if (taskInfo.mTaskStatus == -1 || taskInfo.mTaskStatus == 0 || taskInfo.mTaskStatus == 3) {
            this.addMagnetTaskListener.failed(this.taskId, -1);
            FlashDownEngineImpl.stopTask((int) this.taskId);
        } else if (taskInfo.mTaskStatus != 1) {
        } else {
            if (this.timeout >= mTimeout) {
                this.addMagnetTaskListener.failed(this.taskId, -1);
                FlashDownEngineImpl.stopTask((int) this.taskId);
                return;
            }
            this.handler.postDelayed(this, 1000L);
        }
    }
}
