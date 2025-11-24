package com.chuanyun.downloader.base.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

public class TTBaseMultiItemAdapter<T extends MultiItemEntity> extends BaseMultiItemQuickAdapter<T, BaseViewHolder> {
    public TTBaseMultiItemAdapter(@Nullable List<T> data) {
        super(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, T t) {

    }
}
