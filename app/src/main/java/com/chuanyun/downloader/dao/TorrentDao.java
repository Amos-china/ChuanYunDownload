package com.chuanyun.downloader.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.chuanyun.downloader.models.TTTorrentInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TorrentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertTorrentInfo(TTTorrentInfo torrentInfo);

    @Query("SELECT * FROM torrent_info order by create_time desc")
    Single<List<TTTorrentInfo>> getAllTorrentInfo();

    @Query("SELECT * FROM torrent_info where is_del=0 order by create_time desc")
    Single<List<TTTorrentInfo>> getTorrentInfoAtNotDel();

    @Query("SELECT * FROM torrent_info where is_del=0 and is_like=1 order by create_time desc")
    Single<List<TTTorrentInfo>> getTorrentInfoAtIsLike();

    @Query("SELECT *FROM torrent_info where hash=:hash")
    Single<TTTorrentInfo> getTorrentInfoBy(String hash);


    @Query("Delete FROM torrent_info where hash=:hash")
    Completable deleteTorrentInfo(String hash);

    @Query("update torrent_info set is_del=:isDel where hash=:hash")
    Completable setTorrentInfoDel(String hash,int isDel);


    @Query("SELECT * FROM torrent_info order by create_time desc limit :page,:size")
    Single<List<TTTorrentInfo>> getTorrentInfoListAt(int page,int size);

    @Query("update torrent_info set is_like=:isLike where hash=:hash")
    Completable setTorrentInfoIsLike(int isLike, String hash);

    @Query("SELECT * FROM torrent_info WHERE (:name = '' OR torrent_name LIKE :name) AND is_del = 0 ORDER BY create_time DESC LIMIT :page, :size")
    Single<List<TTTorrentInfo>> getTorrentList(String name,int page, int size);

    @Query("SELECT * FROM torrent_info WHERE ( :name='' OR torrent_name LIKE :name) AND is_del=0 AND is_like=1 ORDER BY create_time DESC LIMIT :page, :size")
    Single<List<TTTorrentInfo>> getLikeTorrentList(String name,int page, int size);

    @Query("update torrent_info set torrent_name=:name where hash=:hash")
    Completable updateTorrentName(String name, String hash);

}
