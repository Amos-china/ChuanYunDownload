package com.chuanyun.downloader.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.chuanyun.downloader.tabbar.home.model.UrlNavModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface UrlNavDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUrlNavInfo(UrlNavModel urlNavModel);

    @Query("SELECT * FROM url_nav_info order by create_time limit :page,:size")
    Single<List<UrlNavModel>> getNavInfoList(int page,int size);

    @Query("DELETE FROM url_nav_info WHERE id = :id")
    Completable deleteNavUrlAt(long id);
}
