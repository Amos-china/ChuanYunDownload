package com.chuanyun.downloader.tabbar.download.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.models.MoreModel;

import java.util.List;

public class DownloadMoreAdapter extends BaseQuickAdapter<MoreModel, BaseViewHolder> {
    public DownloadMoreAdapter(@NonNull List<MoreModel> data) {
        super(R.layout.tabbar_item,data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, MoreModel moreModel) {
        baseViewHolder.setImageResource(R.id.title_img,moreModel.getSec())
                .setText(R.id.title_text,moreModel.getTitle());
    }
}
