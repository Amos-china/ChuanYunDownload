package com.chuanyun.downloader.downloadService;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ghost.downengine.FlashDownEngineImpl;
import com.chuanyun.downloader.core.IAddMagnetTaskListener;
import com.chuanyun.downloader.models.TTTorrentInfo;
import com.chuanyun.downloader.utils.FileOperate;
import com.chuanyun.downloader.utils.FileUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;

public class FCDownloadService extends Service {

    private static HashMap<String, Integer> mMagnetTaskCollections = new HashMap<>();
    private FCDownloadBinder mDownloadBinder = new FCDownloadBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDownloadBinder;
    }


    public class FCDownloadBinder extends Binder {
        public FCDownloadBinder() {}

        public FCDownloadService getDownloadService() {return FCDownloadService.this;}

        public void init(String str, String str2, String str3, String str4) {
            FlashDownEngineImpl.init(str, str2, str3, str4);
        }

        public Observable<Boolean> init() {
            return Observable.create(emitter -> {
                FCTokenHelper.setToken("OTIxMGJ4Y2U0eG9WOHo1dEY4TkxkWkNISHgwYUR2NXE1eTBjQnN5RUhOV0ZBN1Q1cUFkVERneVNyYWJza2o4eENQcTJxNDdDOE9yNmpTTUVySkhwRXA5MWRCbEE=");
                FCTokenHelper.setUid("1140182");
                FCTokenHelper.setVersion(203110);
                        emitter.onNext(true);
                    emitter.onComplete();
            });
        }

        public String getMagnetHash(String str) {
            String str2 = "";
            Matcher matcher = Pattern.compile("[a-zA-Z0-9]{40}").matcher(str);
            if (matcher.find()) {
                str2 = matcher.group(0);
            }
            if (str2.equals("")) {
                Matcher matcher2 = Pattern.compile("[a-zA-Z0-9]{32}").matcher(str);
                if (matcher2.find()) {
                    str2 = matcher2.group(0);
                }
            }
            return (!str2.equals("") || str2 == null) ? str2.toUpperCase() : Integer.toString(str2.hashCode());
        }

        public long addMagnetTask(String str, String str2, String str3, final IAddMagnetTaskListener magnetTaskListener) {
            String magnetHash = getMagnetHash(FCHelper.getMagnetHash(str));
            if (mMagnetTaskCollections.containsKey(magnetHash)) {
                FlashDownEngineImpl.stopTask(mMagnetTaskCollections.get(magnetHash).intValue());
                mMagnetTaskCollections.remove(magnetHash);
            }
            long addMagnetTask = FlashDownEngineImpl.addMagnetTask(magnetHash, str2, str3, FCTokenHelper.getToken());
            mMagnetTaskCollections.put(magnetHash, Integer.valueOf((int) addMagnetTask));
            new Handler().postDelayed(new FCAddMagnetTaskHandler(new Handler(), addMagnetTask, magnetHash, FileOperate.子文本替换(str2 + "/" + str3, "//", "/"),magnetTaskListener), 1000L);
            return addMagnetTask;
        }

        public long parseMagnet(String str, String str2, final IAddMagnetTaskListener magnetTaskListener) {
            String magnetHash = getMagnetHash(FCHelper.getMagnetHash(str));
            String str3 = getMagnetHash(magnetHash) + ".torrent";
            if (mMagnetTaskCollections.containsKey(magnetHash)) {
                FlashDownEngineImpl.stopTask(((Integer)mMagnetTaskCollections.get(magnetHash)).intValue());
                mMagnetTaskCollections.remove(magnetHash);
            }
            long addMagnetTask = FlashDownEngineImpl.addMagnetTask(magnetHash, str2, str3, FCTokenHelper.getToken());
            mMagnetTaskCollections.put(magnetHash, Integer.valueOf((int) addMagnetTask));
            new Handler().postDelayed(new FCAddMagnetTaskHandler(new Handler(), addMagnetTask, magnetHash, FileOperate.子文本替换(str2 + "/" + str3, "//", "/"), magnetTaskListener), 1000L);
            return addMagnetTask;
        }

        public TTTorrentInfo openTorrent(String str) {
            TTTorrentInfo infoFromTorrentFile = FCHelper.getInfoFromTorrentFile(str);
            return infoFromTorrentFile;
        }

        public long addTorrentTask(String str, int i, String str2) {
            return FlashDownEngineImpl.addTorrentTask(str, str2, new int[]{i}, FCTokenHelper.getToken());
        }

        public long getDownloadSize(int i) {
            FCTaskInfo taskInfo = FCHelper.getTaskInfo(i);
            if (taskInfo != null) {
                return taskInfo.mDownloadSize;
            }
            return 0L;
        }

        public int getHealth(int i) {
            FCTaskInfo taskInfo = FCHelper.getTaskInfo(i);
            if (taskInfo != null) {
                return taskInfo.health;
            }
            return 0;
        }

        public long getFileSize(int i) {
            FCTaskInfo taskInfo = FCHelper.getTaskInfo(i);
            if (taskInfo != null) {
                return taskInfo.mFileSize;
            }
            return 0L;
        }

        public int getTaskStatus(int i) {
            int i2;
            FCTaskInfo taskInfo = FCHelper.getTaskInfo(i);
            if (taskInfo == null) {
                return 0;
            }
            switch (taskInfo.mTaskStatus) {
                case -1:
                case 3:
                    i2 = 3;
                    break;
                case 1:
                    i2 = 1;
                    break;
                case 2:
                    i2 = 2;
                    break;
                default:
                    i2 = 0;
                    break;
            }
            return i2;
        }

        public int getRealTaskStatus(int i) {
            FCTaskInfo taskInfo = FCHelper.getTaskInfo(i);
            if (taskInfo != null) {
                return taskInfo.mTaskStatus;
            }
            return 0;
        }

        public long getDownloadSpeed(int i) {
            FCTaskInfo taskInfo = FCHelper.getTaskInfo(i);
            if (taskInfo != null) {
                return taskInfo.mDownloadSpeed;
            }
            return 0L;
        }

        public String getTaskInfo(int i) {
            return FlashDownEngineImpl.getTaskInfo(i);
        }

        public void stopTask(int i) {
            FlashDownEngineImpl.stopTask(i);
        }

        public String getPlayUrl(String str) {
            return "file://" + FileUtils.encodeFilePath(str);
        }

        public String getPlayUrl(int i, int i2, String str) {
            return FlashDownEngineImpl.getPlayUrl(i, i2, str);
        }

        public long addThunderTask(String str, String str2) throws Exception {
            int i;
            String fileName = FCHelper.getFileName(str);
            String thunderDecode = str.startsWith("thunder://") ? FCHelper.thunderDecode(str) : str;
            if (TextUtils.isEmpty(fileName)) {
                fileName = FCHelper.getFileName(thunderDecode);
            }
            if (thunderDecode.startsWith("ed2k://")) {
                i = FlashDownEngineImpl.addEd2kTask(thunderDecode, str2, fileName, FCTokenHelper.getToken());
            } else if (thunderDecode.startsWith("ftp://") || thunderDecode.startsWith("http://") || thunderDecode.startsWith("https://")) {
                i = FlashDownEngineImpl.addHttpTask(thunderDecode, str2, fileName, "", false, 0, FCTokenHelper.getToken());
            } else if (thunderDecode.startsWith("magnet")) {
                return parseMagnet(thunderDecode, str2, null);
            } else {
                throw new Exception("url illegal.");
            }
            return i;
        }

        public long addThunderTask2(String str, String str2, String str3) throws Exception {
            int i;
            String thunderDecode = str.startsWith("thunder://") ? FCHelper.thunderDecode(str) : str;
            if (thunderDecode.startsWith("ed2k://")) {
                i = FlashDownEngineImpl.addEd2kTask(thunderDecode, str2, str3, FCTokenHelper.getToken());
            } else if (thunderDecode.startsWith("ftp://") || thunderDecode.startsWith("http://") || thunderDecode.startsWith("https://")) {
                i = FlashDownEngineImpl.addHttpTask(thunderDecode, str2, str3, "", false, 0, FCTokenHelper.getToken());
            } else if (thunderDecode.startsWith("magnet")) {
                return parseMagnet(thunderDecode, str2, null);
            } else {
                throw new Exception("url illegal.");
            }
            return i;
        }

        public String getTorrentHash(String str) {
            return FCHelper.getInfoFromTorrentFile(str).getHash();
        }

        public int getTorrentFileCount(String str) {
            return FCHelper.getInfoFromTorrentFile(str).getFileCount();
        }

        public String getTorrentMultiFileBaseFolder(String str) {
            return FCHelper.getInfoFromTorrentFile(str).getTorrentName();
        }

        public String getFileName(String str) {
            String fileName = FCHelper.getFileName(str);
            return fileName.equals("") ? "未知文件名" : fileName;
        }
    }
}
