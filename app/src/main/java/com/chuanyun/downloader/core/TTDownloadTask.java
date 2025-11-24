package com.chuanyun.downloader.core;

import android.util.Log;

import com.chuanyun.downloader.models.AppSettingsModel;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.dao.DownloadDao;
import com.chuanyun.downloader.models.TorrentFileInfoModel;
import com.chuanyun.downloader.utils.FileUtils;
import com.chuanyun.downloader.utils.StorageHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class  TTDownloadTask {

    private static final String TAG = "TTDownloadTask";

    private DownloadDao downloadDao;

    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    private List<TorrentFileInfoModel> fileInfoModelList;

    private IDownloadTaskListener downloadTaskListener;
    private IDownloadSuccessListener downloadSuccessListener;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    public void clearDisposable() {
        compositeDisposable.clear();
    }

    public void setDownloadSuccessListener(IDownloadSuccessListener successListener) {
        this.downloadSuccessListener = successListener;
    }

    public void setDownloadTaskListener(IDownloadTaskListener downloadTaskListener) {
        this.downloadTaskListener = downloadTaskListener;
    }

    public List<TorrentFileInfoModel> getFileInfoModelList() {
        return fileInfoModelList;
    }


    public TTDownloadTask() {
        fileInfoModelList = new ArrayList<>();
        downloadDao = App.getApp().getAppDataBase().downloadDao();
        Disposable disposable = downloadDao.resetDownloadTaskStatus()
                .andThen(downloadDao.getStopTaskInfoList())
                .subscribeOn(Schedulers.io())
                .subscribe(list -> fileInfoModelList.addAll(list),Throwable::printStackTrace);
        addDisposable(disposable);
    }

    private int getMaxDownloadCount() {
        return AppSettingsModel.getSettingsModel().getDownloadTaskCount();
    }

    public TorrentFileInfoModel downloadTorrent(TorrentFileInfoModel fileInfoModel) {
        if (fileInfoModel.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
            return fileInfoModel;
        }

        try {
            String downloadPath = StorageHelper.createDownloadDir();
            String videoPath = downloadPath + "/" + fileInfoModel.getHash() + "/" + fileInfoModel.getIndex() + "/";

            fileInfoModel.setVideoPath(videoPath);
            fileInfoModel.setTaskDownloadStatusCallBack(this::reloadUi);
            fileInfoModel.setToken(token);

            boolean canDownload = getDownloadTaskCount() >= getMaxDownloadCount();
            int downloadStatus = canDownload ? TorrentFileInfoModel.DOWNLOAD_STATUS_WAIT : TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING;

            fileInfoModel.setDownloadStatus(downloadStatus);

            if (!fileInfoModel.isDownload()) {
                createDownloadInfo(fileInfoModel,videoPath);
            }

            Thread.sleep(50);

            if (downloadStatus == TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
                fileInfoModel.start();
            }

            downloadTaskListener.reloadItem(fileInfoModel);



        } catch (Exception exception) {
            Log.i(TAG, exception.getMessage());
        }
        return fileInfoModel;
    }

    private void reloadUi(TorrentFileInfoModel infoModel) {
        if (infoModel.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_FINISH) {
            infoModel.stopDownload();
            if (downloadTaskListener != null) {
                downloadTaskListener.removeDownloadTask(infoModel);
            }else {
                fileInfoModelList.remove(infoModel);
            }

            if (downloadSuccessListener != null) {
                downloadSuccessListener.downloadSuccess(infoModel);
            }
            startNexDownload();
        }else {
            if (downloadTaskListener != null) {
                downloadTaskListener.reloadItem(infoModel);
                if (infoModel.getDownloadStatus() != TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
                    infoModel.stopDownload();
                    startNexDownload();
                }
            }
        }
    }

    private TorrentFileInfoModel createDownloadInfo(TorrentFileInfoModel downloadInfo, String downloadPath) {
        if (downloadInfo.isHasSubPath()) {
            downloadPath = downloadPath + downloadInfo.getSubPath() + "/";
        }

        downloadInfo.setFilePath(downloadPath);
        long currentTime = System.currentTimeMillis();
        downloadInfo.setCreateTime(currentTime);
        downloadInfo.setDownload(true);
        downloadDao.insertDownloadInfo(downloadInfo).subscribeOn(Schedulers.io()).subscribe();

        if (downloadTaskListener != null) {
            downloadTaskListener.addDownloadTask(downloadInfo);
        }else {
            fileInfoModelList.add(downloadInfo);
        }

        return downloadInfo;
    }

    //status : 0暂停 1下载中 2完成 3失败 4等待
    public XLTaskInfo getDownloadTaskInfo(long taskId) {
        return XLTaskHelper.getInstance().getTaskInfo(taskId);
    }

    public void downloadAllTask() {
        Disposable disposable = Observable.fromIterable(fileInfoModelList)
                .subscribeOn(Schedulers.newThread())
                .filter(model -> model.getDownloadStatus() != TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING)
                .subscribe(this::downloadTorrent);
        addDisposable(disposable);
    }

    public void stopAllTask() {
        Disposable disposable = Observable.fromIterable(fileInfoModelList)
                .filter(fileInfoModel -> {
                    int status = fileInfoModel.getDownloadStatus();
                    return status == TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING || status == TorrentFileInfoModel.DOWNLOAD_STATUS_WAIT;
                })
                .subscribeOn(Schedulers.io())
                .map(fileInfoModel -> {
                    fileInfoModel.stopDownload();
                    XLTaskHelper.getInstance().stopTask(fileInfoModel.getTaskId());
                    fileInfoModel.setDownloadStatus(TorrentFileInfoModel.DOWNLOAD_STATUS_STOP);
                    fileInfoModel.setSpeed(0);
                    return fileInfoModel;
                })
                .doOnComplete(() ->  downloadTaskListener.reloadList())
                .subscribe();
        addDisposable(disposable);
    }

    public void deleteTask(TorrentFileInfoModel fileInfoModel,boolean deleteFile) {
        if (fileInfoModel.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
            fileInfoModel.setDownloadStatus(TorrentFileInfoModel.DOWNLOAD_STATUS_STOP);
            fileInfoModel.stopDownload();
            String filePath = fileInfoModel.getFilePath() + fileInfoModel.getName();
            XLTaskHelper.getInstance().stopTask(fileInfoModel.getTaskId());
            XLTaskHelper.getInstance().deleteTask(fileInfoModel.getTaskId(),filePath);
        }

        if (deleteFile) {
            String downloadPath = StorageHelper.createDownloadDir();
            String videoPath = downloadPath + "/" + fileInfoModel.getHash() + "/" + fileInfoModel.getIndex();
            FileUtils.deleteFolder(videoPath);
        }

        startNexDownload();
    }

    public void stopDownloadTask(TorrentFileInfoModel fileInfoModel) {
        fileInfoModel.stopDownload();
        XLTaskHelper.getInstance().stopTask(fileInfoModel.getTaskId());
        fileInfoModel.setDownloadStatus(TorrentFileInfoModel.DOWNLOAD_STATUS_STOP);
        fileInfoModel.setSpeed(0);
        downloadDao.updateDownloadInfo(fileInfoModel.getDownloadSize(),
                        fileInfoModel.getDownloadStatus(),
                        fileInfoModel.getInfoId(),fileInfoModel.getTaskId())
                .subscribeOn(Schedulers.io())
                .subscribe();

        startNexDownload();
    }

    public synchronized void startNexDownload() {
        for (TorrentFileInfoModel infoModel: fileInfoModelList) {
            if (infoModel != null && infoModel.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_WAIT) {
                downloadTorrent(infoModel);
                break;
            }
        }
    }

    public int getDownloadTaskCount() {
        int count = 0;
        for (int i = 0; i < fileInfoModelList.size(); i ++) {
            TorrentFileInfoModel fileInfoModel = fileInfoModelList.get(i);
            if (fileInfoModel.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
                count ++;
            }
        }
        return count;
    }
}
