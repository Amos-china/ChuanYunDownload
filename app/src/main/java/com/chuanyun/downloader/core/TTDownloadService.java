package com.chuanyun.downloader.core;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.XLTokenHelper;
import com.chuanyun.downloader.models.TTTorrentInfo;
import com.chuanyun.downloader.models.TorrentFileInfoModel;
import com.chuanyun.downloader.utils.FileUtils;
import com.chuanyun.downloader.utils.StorageHelper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TTDownloadService {
    private static String TAG = "TTDownloadService";
    private static TTDownloadService instance = new TTDownloadService();

    private TTParseTorrent parseTorrent;
    private TTDownloadTask downloadTask;


    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    public void clearDisposable() {
        compositeDisposable.clear();
    }

    public void setDownloadTaskListener(IDownloadTaskListener downloadTaskListener) {
        downloadTask.setDownloadTaskListener(downloadTaskListener);
    }

    public void setDownloadSuccessListener(IDownloadSuccessListener downloadSuccessListener) {
        downloadTask.setDownloadSuccessListener(downloadSuccessListener);
    }

    public List<TorrentFileInfoModel> getFileInfoModelList() {
        return downloadTask.getFileInfoModelList();
    }

    private TTDownloadService() {}

    public static TTDownloadService getInstance() {
        return instance;
    }

    @SuppressLint("CheckResult")
    public void initXL(Context context) {
        parseTorrent = new TTParseTorrent();

        Intent serviceIntent = new Intent(context,AppDownloadService.class);
        context.bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE);

        XLTaskHelper.init(context);

        Disposable disposable = initVersion().subscribeOn(Schedulers.io()).subscribe(aBoolean -> {},throwable -> {});
        addDisposable(disposable);

    }

    public long parseMagnet(String magnet, IAddMagnetTaskListener addMagnetTaskListener) {
        return parseTorrent.parseMagnet(magnet, addMagnetTaskListener);
    }

    public TTTorrentInfo openTorrent(String torrentPath, String magnet,boolean isHistory) {
        return parseTorrent.openTorrent(torrentPath,magnet,isHistory);
    }

    public TorrentFileInfoModel downloadTorrent(TorrentFileInfoModel fileInfoModel) {
        return downloadTask.downloadTorrent(fileInfoModel);
    }

    public String getVideFileUrl(TorrentFileInfoModel downloadInfo) {
        String videoPath = downloadInfo.getFilePath() + downloadInfo.getName();
        return XLTaskHelper.getInstance().getLocalUrl(videoPath);
    }

    public void deleteTempTask(TorrentFileInfoModel fileInfoModel) {
        String downloadPath = StorageHelper.createTempDir();
        String videoPath = downloadPath + "/" + fileInfoModel.getTorrentName() + "/" + fileInfoModel.getIndex() + "/";
        if (fileInfoModel.isHasSubPath()) {
            videoPath = videoPath + fileInfoModel.getSubPath() + "/";
        }
        XLTaskHelper.getInstance().deleteTask(fileInfoModel.getTaskId(),videoPath + fileInfoModel.getName());
        deleteTempFolder();
    }

    public static void deleteTempFolder() {
        String downloadPath = StorageHelper.createTempDir();
        FileUtils.deleteFolder(downloadPath);
    }

    public String getOnlinePlayUrl(TorrentFileInfoModel fileInfoModel) {
        try {
            String downloadPath = StorageHelper.createTempDir();
            String videoPath = downloadPath + "/" + fileInfoModel.getTorrentName() + "/" + fileInfoModel.getIndex() + "/";

            long task;
            if (fileInfoModel.getFileMagnetType() == 1) {
                task = XLTaskHelper.getInstance().addTorrentTask(fileInfoModel.getTorrentPath(),videoPath,new int[]{fileInfoModel.getIndex()});
            }else {
                task = XLTaskHelper.getInstance().addThunderTask(fileInfoModel.getMagnet(),videoPath,fileInfoModel.getName(),downloadTask.getToken(),0);
            }

            fileInfoModel.setTaskId(task);

            if (fileInfoModel.isHasSubPath()) {
                videoPath = videoPath + fileInfoModel.getSubPath() + "/";
            }

            return XLTaskHelper.getInstance().getLocalUrl(videoPath + fileInfoModel.getName());

        } catch (Exception exception) {
            Log.i(TAG, exception.getMessage());
            return "";
        }
    }

    public void stopDownloadTask(TorrentFileInfoModel fileInfoModel) {
        downloadTask.stopDownloadTask(fileInfoModel);
    }

    public void deleteTask(TorrentFileInfoModel fileInfoModel,boolean deleteFile) {
        downloadTask.deleteTask(fileInfoModel,deleteFile);
    }

    public long getDownloadTaskSpeed(long taskId) {
        return XLTaskHelper.getInstance().getTaskInfo(taskId).mDownloadSpeed;
    }

    private Observable<Boolean> initVersion() {
        return Observable.create(emitter -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
                    .readTimeout(30, TimeUnit.SECONDS)   // 读取超时
                    .writeTimeout(15, TimeUnit.SECONDS)  // 写入超时
                    .build();

            Request request = new Request.Builder()
                    .url("http://api.1foo.com/flashConfig.php")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                    XLTokenHelper.setVt(jsonObject.getIntValue("version"));
                    XLTokenHelper.setUid(jsonObject.getString("uid"));
                    downloadTask.setToken(jsonObject.getString("token"));
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                    System.out.println("请求失败: " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
            emitter.onComplete();
        });

    }

    public void destroy(Context context) {
        downloadTask.clearDisposable();
        parseTorrent.clearDisposable();
        clearDisposable();

        if (isBound) {
            context.unbindService(serviceConnection);
            isBound = false;
        }
    }

    public TTTorrentInfo openLink(String url,boolean isHistory) {
        return parseTorrent.openLink(url,isHistory);
    }

    public void downloadAllTask() {
        downloadTask.downloadAllTask();
    }

    public void stopAllTask() {
        downloadTask.stopAllTask();
    }

    public boolean checkFileDownload(TorrentFileInfoModel fileInfoModel) {
        if (fileInfoModel.getFileMagnetType() == 1) {
            return StorageHelper.doesPathExist(fileInfoModel.getTorrentPath());
        }
        return true;
    }

    public boolean checkTorrentFile(TTTorrentInfo torrentInfo) {
        if (torrentInfo.getMagnetType() == 1) {
            return StorageHelper.doesPathExist(torrentInfo.getPath());
        }
        return true;
    }

    private AppDownloadService downloadService;
    private boolean isBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AppDownloadService.AppDownloadBinder binder = (AppDownloadService.AppDownloadBinder) iBinder;
            downloadService = binder.getService();
            downloadTask = downloadService.createDownloadTask();
            Log.i(TAG, "onServiceConnected: 初始化");
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    public int getDownloadTaskCount() {
        return downloadTask.getDownloadTaskCount();
    }
}
