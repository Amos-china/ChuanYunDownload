package com.chuanyun.downloader.tabbar.download.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.adapter.TTBaseQuickAdapter;
import com.chuanyun.downloader.models.TorrentFileInfoModel;
import com.chuanyun.downloader.utils.FileTypeUtils;
import com.chuanyun.downloader.utils.FileUtils;

import java.util.List;

public class DownloadIndexAdapter extends TTBaseQuickAdapter<TorrentFileInfoModel> {
    public DownloadIndexAdapter(@NonNull List<TorrentFileInfoModel> data) {
        super(R.layout.item_download_success,data);

    }

    public void reloadItem(int position) {
        notifyItemChanged(position,"change");
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, TorrentFileInfoModel downloadInfo) {
        if (downloadInfo != null) {

            String fileSize = FileUtils.getFormatSize(downloadInfo.getSize());
            baseViewHolder.setImageResource(R.id.file_type_im, FileTypeUtils.getFileTypeRes(downloadInfo));

            SuperTextView playTv = baseViewHolder.getView(R.id.play_tv);
            if (downloadInfo.getFileSuffixType() == 1) {
                playTv.setText("立即播放");
            }else {
                playTv.setText("立即打开");
            }

            ImageView downloadIm = baseViewHolder.getView(R.id.download_im);

            if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
                downloadIm.setImageResource(R.mipmap.stop_icon_2);

                String downloadSize = FileUtils.getFormatSize(downloadInfo.getDownloadSize());
                String speedStr = "";
                if (downloadInfo.getSpeed() == 0) {
                    speedStr = "连接中";
                }else {
                    String downloadSpeed = FileUtils.getFormatSize(downloadInfo.getSpeed());
                    speedStr = downloadSpeed + "/s";
                }

                baseViewHolder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + downloadSize + "/" + fileSize)
                        .setText(R.id.speed_tv,speedStr);

            }else if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_FINISH) {
                baseViewHolder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + fileSize)
                        .setText(R.id.speed_tv,"下载完成");
                downloadIm.setImageResource(R.mipmap.more_icon);
            }else if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_STOP){
                downloadIm.setImageResource(R.mipmap.download_icon);
                String downloadSize = FileUtils.getFormatSize(downloadInfo.getDownloadSize());
                baseViewHolder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + downloadSize + "/" + fileSize)
                        .setText(R.id.speed_tv,"已暂停");
            }else if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_FAIL){
                downloadIm.setImageResource(R.mipmap.download_icon);
                String downloadSize = FileUtils.getFormatSize(downloadInfo.getDownloadSize());
                baseViewHolder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + downloadSize + "/" + fileSize)
                        .setText(R.id.speed_tv,"下载失败");
            }else {
                downloadIm.setImageResource(R.mipmap.download_icon);
                String downloadSize = FileUtils.getFormatSize(downloadInfo.getDownloadSize());
                baseViewHolder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + downloadSize + "/" + fileSize)
                        .setText(R.id.speed_tv,"等待中");
            }
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TorrentFileInfoModel downloadInfo, @NonNull List<?> payloads) {
        if (payloads.isEmpty()) {
            super.convert(holder, downloadInfo, payloads);
        }else {
            String downloadSize = FileUtils.getFormatSize(downloadInfo.getDownloadSize());
            String fileSize = FileUtils.getFormatSize(downloadInfo.getSize());

            ImageView downloadIm = holder.getView(R.id.download_im);

            if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_DOWNLOADING) {
                downloadIm.setImageResource(R.mipmap.stop_icon_2);

                String speedStr = "";
                if (downloadInfo.getSpeed() == 0) {
                    speedStr = "连接中";
                }else {
                    String downloadSpeed = FileUtils.getFormatSize(downloadInfo.getSpeed());
                    speedStr = downloadSpeed + "/s";
                }

                holder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + downloadSize + "/" + fileSize)
                        .setText(R.id.speed_tv,speedStr);
            }else if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_FINISH) {
                holder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + fileSize)
                        .setText(R.id.speed_tv,"下载完成");
                downloadIm.setImageResource(R.mipmap.more_icon);
            }else if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_STOP){
                downloadIm.setImageResource(R.mipmap.download_icon);
                holder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + downloadSize + "/" + fileSize)
                        .setText(R.id.speed_tv,"已暂停");
            }else if (downloadInfo.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_FAIL){
                downloadIm.setImageResource(R.mipmap.download_icon);
                holder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + downloadSize + "/" + fileSize)
                        .setText(R.id.speed_tv,"下载失败");
            }else {
                downloadIm.setImageResource(R.mipmap.download_icon);
                holder.setText(R.id.download_name_tv,downloadInfo.getName())
                        .setText(R.id.size_tv,"大小:" + downloadSize + "/" + fileSize)
                        .setText(R.id.speed_tv,"等待中");
            }
        }
    }
}
