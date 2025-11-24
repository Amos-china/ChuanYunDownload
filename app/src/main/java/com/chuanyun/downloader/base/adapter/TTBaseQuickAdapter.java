package com.chuanyun.downloader.base.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chuanyun.downloader.R;

import java.util.List;

public abstract class TTBaseQuickAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public TTBaseQuickAdapter(@LayoutRes int layoutId, @NonNull List<T> data) {
        super(layoutId,data);
    }



    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, T t) {

    }

    public void addEmptyView() {
        setEmptyView(R.layout.view_empty);
    }

    public void addLoadMore(OnLoadMoreListener onLoadMoreListener) {
        getLoadMoreModule().setOnLoadMoreListener(onLoadMoreListener);
    }

    public void endLoadMore(int listSize) {
        if (listSize == 0) {
            loadMoreEnd();
        }else {
            loadMoreComplete();
        }
    }

    public void loadMoreEnd() {
        getLoadMoreModule().loadMoreEnd();
    }

    public void loadMoreComplete() {
        getLoadMoreModule().loadMoreComplete();
    }
}
