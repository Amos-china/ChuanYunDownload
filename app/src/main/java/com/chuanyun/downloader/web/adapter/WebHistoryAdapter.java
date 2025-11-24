package com.chuanyun.downloader.web.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.adapter.TTBaseQuickAdapter;
import com.chuanyun.downloader.models.WebHistoryInfo;

import java.util.List;

public class WebHistoryAdapter extends TTBaseQuickAdapter<WebHistoryInfo> implements LoadMoreModule {
    public WebHistoryAdapter(List<WebHistoryInfo> data) {
        super(R.layout.item_web_history,data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, WebHistoryInfo webHistoryInfo) {
        boolean hasIcon = !TextUtils.isEmpty(webHistoryInfo.getByteImage());
        ImageView iconImageView = baseViewHolder.getView(R.id.icon_im);
        if (hasIcon) {
            iconImageView.setImageBitmap(webHistoryInfo.getImageBitmap());
        }else {
            iconImageView.setImageResource(R.mipmap.url_sear_im);
        }

        baseViewHolder.setText(R.id.web_title_tv,webHistoryInfo.getTitle())
                .setText(R.id.web_url_tv,webHistoryInfo.getUrl());
    }
}
