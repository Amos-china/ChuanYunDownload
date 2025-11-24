package com.chuanyun.downloader.dao;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.chuanyun.downloader.models.TTTorrentInfo;
import com.chuanyun.downloader.models.TorrentFileInfoModel;
import com.chuanyun.downloader.models.WebHistoryInfo;
import com.chuanyun.downloader.tabbar.home.model.UrlNavModel;
import com.chuanyun.downloader.utils.StorageHelper;

@Database(entities = {TTTorrentInfo.class, TorrentFileInfoModel.class, WebHistoryInfo.class, UrlNavModel.class}, version = 3, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    private static String dataBaseName = "chuanyun.db";

    public abstract TorrentDao torrentDao();
    public abstract DownloadDao downloadDao();
    public abstract WebHistoryDao webHistoryDao();
    public abstract UrlNavDao urlNavDao();

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS url_nav_info (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "tag_name TEXT, " +
                            "url TEXT, " +
                            "name TEXT, " +
                            "create_time INTEGER NOT NULL DEFAULT 0" +
                            ")"
            );
        }
    };

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE torrent_info ADD COLUMN is_like INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static AppDataBase createDataBase(Context context) {
        String path = StorageHelper.createDataBaseDir() + dataBaseName;
        return Room.databaseBuilder(context,AppDataBase.class,path)
                .addMigrations(MIGRATION_1_2,MIGRATION_2_3)
                .build();
    }
}
