package com.chuanyun.downloader.tabbar.torrent.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.adapter.TTBaseQuickAdapter;
import com.chuanyun.downloader.models.TTTorrentInfo;

import java.text.SimpleDateFormat;
import java.util.List;

public class TorrentHistoryAdapter extends TTBaseQuickAdapter<TTTorrentInfo> implements LoadMoreModule {
    public TorrentHistoryAdapter(List<TTTorrentInfo> data) {
        super(R.layout.item_torrent_history,data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, TTTorrentInfo torrentInfo) {
        if (torrentInfo != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedTime = sdf.format(torrentInfo.getCreateTime());
            baseViewHolder.setText(R.id.name_tv,torrentInfo.getTorrentName())
                    .setText(R.id.time_tv,formattedTime);
        }
    }
}
