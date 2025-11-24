package com.chuanyun.downloader.tabbar.home.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.adapter.TTBaseQuickAdapter;
import com.chuanyun.downloader.models.RecommendURLModel;

import java.util.List;

public class HomeRvAdapter extends TTBaseQuickAdapter<RecommendURLModel> {
    public HomeRvAdapter(@Nullable List<RecommendURLModel> data) {
        super(R.layout.item_url_nav,data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, RecommendURLModel recommendURLModel) {
        super.convert(baseViewHolder, recommendURLModel);

        baseViewHolder.setText(R.id.tag_stv,recommendURLModel.getName());
    }

}
