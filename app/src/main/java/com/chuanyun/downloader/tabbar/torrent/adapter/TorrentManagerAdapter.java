package com.chuanyun.downloader.tabbar.torrent.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.adapter.TTBaseQuickAdapter;
import com.chuanyun.downloader.models.TTTorrentInfo;

import java.text.SimpleDateFormat;
import java.util.List;

public class TorrentManagerAdapter extends TTBaseQuickAdapter<TTTorrentInfo> {

    public static final String RELOAD_SELECT = "RELOAD_SELECT";

    public TorrentManagerAdapter(List<TTTorrentInfo> data) {
        super(R.layout.item_manager_torrent,data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, TTTorrentInfo torrentInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime = sdf.format(torrentInfo.getCreateTime());
        String createTime = "创建时间:" + formattedTime;

        Log.i("TAG", "convert: " + torrentInfo.getSize());
        String size = "大小:" + torrentInfo.getSize();

        int src = torrentInfo.isSelect() ? R.mipmap.check_s : R.mipmap.check_n;

        baseViewHolder.setText(R.id.torrent_name_tv,torrentInfo.getTorrentName())
                .setText(R.id.torrent_size_tv,createTime + "\n" + size)
                .setImageResource(R.id.check_im,src);

        ImageView collectIm = baseViewHolder.findView(R.id.collect_im);
        collectIm.setVisibility(torrentInfo.getIsLike() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TTTorrentInfo item, @NonNull List<?> payloads) {
        if (payloads.isEmpty()) {
            super.convert(holder, item, payloads);
        }else {
            int src = item.isSelect() ? R.mipmap.check_s : R.mipmap.check_n;
            holder.setImageResource(R.id.check_im,src);
        }

    }
}
