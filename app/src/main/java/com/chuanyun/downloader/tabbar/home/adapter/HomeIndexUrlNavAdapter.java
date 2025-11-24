package com.chuanyun.downloader.tabbar.home.adapter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.adapter.TTBaseQuickAdapter;
import com.chuanyun.downloader.tabbar.home.model.UrlNavModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class HomeIndexUrlNavAdapter extends TTBaseQuickAdapter<UrlNavModel> {
    public HomeIndexUrlNavAdapter(List<UrlNavModel> data) {
        super(R.layout.item_nav_url_manager_view,data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, UrlNavModel urlNavModel) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.i("TAG", "convert: " + urlNavModel.getCreateTime());
        String formattedTime = "添加日期:" + sdf.format(urlNavModel.getCreateTime());
        baseViewHolder.setText(R.id.title_tv,urlNavModel.getName())
                .setText(R.id.url_tv,urlNavModel.getUrl())
                .setText(R.id.time_tv,formattedTime);
    }
}
