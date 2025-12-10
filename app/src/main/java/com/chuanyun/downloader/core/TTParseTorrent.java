package com.chuanyun.downloader.core;

import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xunlei.downloadlib.XLDownloadManager;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentInfo;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.dao.TorrentDao;
import com.chuanyun.downloader.eventBusModel.TorrentManagerEvent;
import com.chuanyun.downloader.models.TTTorrentInfo;
import com.chuanyun.downloader.models.TorrentFileInfoModel;
import com.chuanyun.downloader.utils.FileTypeUtils;
import com.chuanyun.downloader.utils.FileUtils;
import com.chuanyun.downloader.utils.HashUtils;
import com.chuanyun.downloader.utils.StorageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;

public class TTParseTorrent {

    private HashMap<String, Long> magnetTaskMap = new HashMap<>();

    public TTParseTorrent(){}

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    public void clearDisposable() {
        compositeDisposable.clear();
    }

    public long parseMagnet(String magnet, IAddMagnetTaskListener addMagnetTaskListener) {
        StorageHelper.createTorrentDir();
        String torrentPath = StorageHelper.createTorrentDir();
        String magnetHash = getMagnetHash(magnet) + ".torrent";
        return addMagnetTask(magnet,torrentPath,magnetHash,addMagnetTaskListener);
    }

    private long addMagnetTask(String magnet, String path, String magnetHash, IAddMagnetTaskListener addMagnetTaskListener) {
        long code = 0;
        if (magnetTaskMap.containsKey(magnet)) {
            XLTaskHelper.getInstance().stopTask(magnetTaskMap.get(magnet).longValue());
            magnetTaskMap.remove(magnet);
        }

        try {
            code = XLTaskHelper.getInstance().addMagnetTask(magnet,path,magnetHash);
        } catch (Exception e) {
            code = -1;
        }

        magnetTaskMap.put(magnet, code);
        new Handler().postDelayed(new IAddMagnetTaskHandler(new Handler(),code,magnet,path + magnetHash,addMagnetTaskListener),1000L);
        return code;
    }

    private String getMagnetHash(String magnet) {
        String hash = "";
        Matcher matcher = Pattern.compile("[a-zA-Z0-9]{40}").matcher(magnet);
        if (matcher.find()) {
            hash = matcher.group(0);
        }
        if (hash.equals("")) {
            Matcher matcher1 = Pattern.compile("[a-zA-Z0-9]{32}").matcher(magnet);
            if (matcher1.find()) {
                hash = matcher1.group(0);
            }
        }
        return (!hash.equals("") || hash == null) ? hash.toUpperCase() : Integer.toString(hash.hashCode());
    }

    public TTTorrentInfo openLink(String url, boolean isHistory) {
        if (url.startsWith("ed2k://")) {
            return openEd2kUrl(url,isHistory);
        }else if (url.startsWith("thunder://")) {
            return openThunderLink(url,isHistory);
        }else if (url.startsWith("ftp://") || url.startsWith("http://") || url.startsWith("https://")) {
            return openOtherLink(url,isHistory);
        }
        return null;
    }


    public TTTorrentInfo openOtherLink(String url,boolean isHistory) {
        String fileName = XLTaskHelper.getInstance().getFileName(url);
        String hash = HashUtils.getMd5Hash(url);
        return parseData(url,fileName,0,hash,isHistory);
    }

    public TTTorrentInfo openThunderLink(String url,boolean isHistory) {
        String xlStr = XLDownloadManager.getInstance().parserThunderUrl(url);
        if (xlStr.startsWith("ed2k://")) {
            return openEd2kUrl(xlStr,isHistory);
        }else if (xlStr.startsWith("magnet:?")){
            TTTorrentInfo ttTorrentInfo = new TTTorrentInfo();
            ttTorrentInfo.setMagnet(xlStr);
            return ttTorrentInfo;
        }else {
            return openOtherLink(xlStr,isHistory);
        }
    }

    public TTTorrentInfo openEd2kUrl(String url, boolean isHistory) {
        String regex = "ed2k://\\|file\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (!matcher.find()) {return null;}

        String fileName = matcher.group(1);
        String fileSize = matcher.group(2);
        long size = Long.parseLong(fileSize);
        String fileHash = matcher.group(3);

        return parseData(url,fileName,size,fileHash,isHistory);
    }

    private TTTorrentInfo parseData(String url,String fileName,long size, String hash, boolean isHistory) {
        TTTorrentInfo info = new TTTorrentInfo();
        assert hash != null;
        info.setHash(hash);
        info.setMagnet(url);
        info.setTorrentName(fileName);
        info.setDownload(true);
        info.setMagnetType(2);
        info.setSize(FileUtils.getFormatSize(size));
        info.setIsDel(0);
        info.setFileCount(1);
        info.setPath(url);
        info.setCreateTime(System.currentTimeMillis());


        TorrentFileInfoModel fileInfoModel = new TorrentFileInfoModel();
        fileInfoModel.setFileMagnetType(2);
        fileInfoModel.setInfoId(hash + "0");
        fileInfoModel.setName(fileName);
        fileInfoModel.setIndex(0);
        fileInfoModel.setRealIndex(0);
        fileInfoModel.setSize(size);
        fileInfoModel.setDownloadStatus(0);

        fileInfoModel.setSelect(false);
        fileInfoModel.setFileSuffixType(FileTypeUtils.getFileTypeAt(fileInfoModel.getName()));
        fileInfoModel.setHasSubPath(false);
        fileInfoModel.setHash(info.getHash());
        fileInfoModel.setMagnet(url);
        fileInfoModel.setTorrentPath(url);
        fileInfoModel.setTorrentName(info.getTorrentName());

        List<TorrentFileInfoModel> fileInfoModelList = new ArrayList<>();
        fileInfoModelList.add(fileInfoModel);

        info.setFileModelList(fileInfoModelList);

        if (isHistory) {
            return info;
        }

        TorrentDao torrentDao = App.getApp().getAppDataBase().torrentDao();
        Disposable disposable = torrentDao.insertTorrentInfo(info)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> EventBus.getDefault().post(new TorrentManagerEvent()))
                .subscribe(() -> {}, throwable -> {});
        addDisposable(disposable);
        return info;
    }


    public TTTorrentInfo openTorrent(String torrentPath, String magnet,boolean isHistory) {
        TorrentInfo torrentInfo =  XLTaskHelper.getInstance().getTorrentInfo(torrentPath);

        String torrentJson = JSON.toJSONString(torrentInfo);
        TTTorrentInfo info = JSON.parseObject(torrentJson,TTTorrentInfo.class);

        info.setMagnet(magnet);
        info.setPath(torrentPath);
        info.setIsDel(0);
        info.setDownload(true);
        info.setMagnetType(1);

        long currentTime = System.currentTimeMillis();
        info.setCreateTime(currentTime);

        long totalSize = 0;

        if (info.getFileModelList() == null) {
            info.setFileModelList(new ArrayList<>());
        }

        for (int i = 0; i < info.getFileModelList().size(); i ++) {
            TorrentFileInfoModel fileModel = info.getFileModelList().get(i);
            fileModel.setSelect(false);
            fileModel.setFileSuffixType(FileTypeUtils.getFileTypeAt(fileModel.getName()));
            fileModel.setHasSubPath(!TextUtils.isEmpty(fileModel.getSubPath()));
            fileModel.setHash(info.getHash());
            fileModel.setMagnet(magnet);
            fileModel.setTorrentPath(torrentPath);
            fileModel.setInfoId(info.getHash() + fileModel.getIndex());
            fileModel.setTorrentName(info.getTorrentName());
            fileModel.setFileMagnetType(info.getMagnetType());
            totalSize += fileModel.getSize();
        }

        if (TextUtils.isEmpty(torrentInfo.mMultiFileBaseFolder)) {
            info.setTorrentName(info.getFileModelList().get(0).getName());
        }

        info.setSize(FileUtils.getFormatSize(totalSize));

        if (isHistory) {
            return info;
        }

        TorrentDao torrentDao = App.getApp().getAppDataBase().torrentDao();
        Disposable disposable = torrentDao.insertTorrentInfo(info)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> EventBus.getDefault().post(new TorrentManagerEvent()))
                .subscribe(() -> {}, throwable -> {});
        addDisposable(disposable);

        return info;
    }
}