package com.chuanyun.downloader.tabbar.download.ui;

import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.fragment.BaseLazyFragment;
import com.chuanyun.downloader.dao.DownloadDao;
import com.chuanyun.downloader.dao.TorrentDao;

public class DownloadTorrentFileFragment extends BaseLazyFragment {

    private TorrentDao torrentDao;
    private DownloadDao downloadDao;

    private int page = 0;

    @Override
    protected int setContentView() {
        return 0;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void lazyLoad() {
        torrentDao = App.getApp().getAppDataBase().torrentDao();
        downloadDao = App.getApp().getAppDataBase().downloadDao();
    }
}
