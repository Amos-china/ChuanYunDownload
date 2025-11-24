package com.chuanyun.downloader.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.chuanyun.downloader.models.WebHistoryInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface WebHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertHistoryInfo(WebHistoryInfo webHistoryInfo);

    @Query("SELECT * FROM web_history_info where del=0 order by create_time desc limit :page,:size")
    Single<List<WebHistoryInfo>> getHistoryInfo(int page, int size);

    @Query("Delete FROM web_history_info")
    Completable clearTable();

    @Query("Delete FROM web_history_info where url=:url")
    Completable deleteHistoryAt(String url);

    @Query("update web_history_info set del=1")
    Completable setWebHistoryInfoAtDelete();

    @Query("Delete FROM web_history_info where collect!=1")
    Completable deleteHistory();

    @Query("SELECT * FROM web_history_info where collect=1 order by create_time desc limit :page,:size")
    Single<List<WebHistoryInfo>> getCollectWebHistoryInfo(int page,int size);

    @Query("update web_history_info set collect=:collect")
    Completable updateWebHistoryInfoCollect(int collect);

    @Query("update web_history_info set collect=:collect where url=:url")
    Completable setWebHistoryInfoCollect(int collect, String url);

    @Query("SELECT * FROM web_history_info where url=:url")
    Single<WebHistoryInfo> getWebHistoryInfoAtUrl(String url);


}
