package com.chuanyun.downloader.tabbar.home.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.adapter.TTBaseQuickAdapter;
import com.chuanyun.downloader.models.TorrentFileInfoModel;
import com.chuanyun.downloader.utils.FileTypeUtils;
import com.chuanyun.downloader.utils.FileUtils;

import java.text.DecimalFormat;
import java.util.List;

public class TorrentDetailAdapter extends TTBaseQuickAdapter<TorrentFileInfoModel> {

    public static final String RELOAD_SELECT = "select";

    public TorrentDetailAdapter(@NonNull List<TorrentFileInfoModel> data) {
        super(R.layout.item_torrent_detail,data);
    }

    //0：未知  1：视频 2：图片 3.音频文件 4.安装包 5：压缩文件 6.文本文件 7.url 8:apk
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, TorrentFileInfoModel fileModel) {
        if (fileModel != null) {
            int checkImSrc;
            TextView statusTv = baseViewHolder.findView(R.id.status_tv);
            if (fileModel.isDownload()) {
                checkImSrc = R.mipmap.no_check;
                statusTv.setVisibility(View.VISIBLE);
                if (fileModel.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_FINISH) {
                    statusTv.setText("下载完成");
                    statusTv.setTextColor(getContext().getColor(R.color.app_color));
                }else if (fileModel.getDownloadStatus() == TorrentFileInfoModel.DOWNLOAD_STATUS_FAIL){
                    statusTv.setText("下载失败");
                    statusTv.setTextColor(getContext().getColor(R.color.red));
                }else {
                    // 计算下载进度的百分比
                    float progress = ((float) fileModel.getDownloadSize() / fileModel.getSize()) * 100;
                    DecimalFormat df = new DecimalFormat("##0.0");
                    String formattedProgress = df.format(progress);
                    statusTv.setText("已下载：" + formattedProgress + "%");
                    statusTv.setTextColor(getContext().getColor(R.color.app_color));
                }
            }else {
                checkImSrc = fileModel.isSelect() ? R.mipmap.check_s : R.mipmap.check_n;
                statusTv.setVisibility(View.INVISIBLE);
            }

            baseViewHolder.setText(R.id.name_tv,fileModel.getName())
                    .setText(R.id.size_tv,"大小:" + FileUtils.getFormatSize(fileModel.getSize()))
                    .setImageResource(R.id.file_type_im, FileTypeUtils.getFileTypeRes(fileModel))
                    .setImageResource(R.id.check_im,checkImSrc);

            SuperTextView playTv = baseViewHolder.getView(R.id.play_tv);

            if (fileModel.getFileSuffixType() == 1) {
                playTv.setVisibility(View.VISIBLE);
            }else {
                playTv.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TorrentFileInfoModel item, @NonNull List<?> payloads) {
        if (payloads.isEmpty()) {
            super.convert(holder, item, payloads);
        }else {
            int checkImSrc;
            if (item.isDownload()) {
                checkImSrc = R.mipmap.no_check;
            }else {
                checkImSrc = item.isSelect() ? R.mipmap.check_s : R.mipmap.check_n;
            }
            holder.setImageResource(R.id.check_im,checkImSrc);
        }
    }
}
