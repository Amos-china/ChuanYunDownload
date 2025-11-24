package com.chuanyun.downloader.player;

import android.content.Context;
import android.content.Intent;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.models.TorrentFileInfoModel;

public class VideoPlayerActivity extends BaseActivity {

    public static final String VIDEO_PATH = "VIDEO_PATH";
    public static final String VIDEO_NAME = "VIDEO_NAME";

    private TorrentFileInfoModel torrentFileInfoModel;


    public static void openPlayer(Context context, String videPath, String videoName) {
        Intent intent = new Intent(context,VideoPlayerActivity.class);
//        intent.putExtra(TorrentFileInfoModel.INTENT_FILE_INFO_MODEL,torrentFileInfoModel);
        intent.putExtra(VIDEO_PATH, videPath);
        intent.putExtra(VIDEO_NAME, videoName);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initView() {
        super.initView();

    }

}
