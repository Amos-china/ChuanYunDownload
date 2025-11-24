package com.chuanyun.downloader.models;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class GroupModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "create_time")
    private long createTime;

    @ColumnInfo(name = "file_num")
    private int fileNum;


}
