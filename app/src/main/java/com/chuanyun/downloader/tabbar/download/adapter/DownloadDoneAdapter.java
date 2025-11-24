package com.chuanyun.downloader.tabbar.download.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.adapter.TTBaseQuickAdapter;
import com.chuanyun.downloader.models.BehaviorFileInfoModel;

import java.util.List;

public class DownloadDoneAdapter extends TTBaseQuickAdapter<BehaviorFileInfoModel> {
    public DownloadDoneAdapter(List<BehaviorFileInfoModel> data) {
        super(R.layout.item_file_download,data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, BehaviorFileInfoModel behaviorFileInfoModel) {

    }
}
