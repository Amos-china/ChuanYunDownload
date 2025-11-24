package com.chuanyun.downloader.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.chuanyun.downloader.models.TorrentFileInfoModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertDownloadInfo(TorrentFileInfoModel fileInfoModel);

    @Query("SELECT * FROM download_info")
    Single<List<TorrentFileInfoModel>> getAllDownloadInfo();

    @Query("SELECT * FROM download_info where info_id=:infoId")
    Single<TorrentFileInfoModel> getDownloadTaskInfo(String infoId);

    @Query("update download_info set download_size=:downloadSize,download_status=:downloadStatus, task_id=:taskId where info_id=:infoId")
    Completable updateDownloadInfo(long downloadSize, int downloadStatus, String infoId,long taskId);

    @Query("Delete FROM download_info where info_id=:infoId")
    Completable deleteDownloadInfo(String infoId);

    @Query("SELECT * FROM download_info where download_status=2")
    Single<List<TorrentFileInfoModel>> getCompleteTaskInfoList();

    @Query("SELECT * FROM download_info where (download_status=1 or download_status=0)")
    Single<List<TorrentFileInfoModel>> getNotCompleteTaskInfoList();

    @Query("SELECT * FROM download_info where download_status=1")
    Single<List<TorrentFileInfoModel>> getDownloadingTaskInfoList();

    @Query("update download_info set download_status=0 where download_status=1")
    Completable resetDownloadTaskStatus();

    @Query("SELECT * FROM download_info where download_status=0 or download_status=3 or download_status=4")
    Single<List<TorrentFileInfoModel>> getStopTaskInfoList();

    @Query("SELECT * FROM download_info where hash=:hash and download_status=2")
    Single<List<TorrentFileInfoModel>> getTorrentInfoDownloadFinish(String hash);
}
