package com.chuanyun.downloader.tabbar.download.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.models.MoreModel;
import com.chuanyun.downloader.popup.TTBaseBottomPopupView;
import com.chuanyun.downloader.tabbar.download.adapter.DownloadMoreAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DownloadTaskMoreSheet extends TTBaseBottomPopupView {

    public interface ItemClickListener {
        void clickIndex(int index);
    }

    @BindView(R.id.popup_rv)
    RecyclerView itemRv;

    private DownloadMoreAdapter downloadMoreAdapter;
    private ItemClickListener itemClickListener;



    public DownloadTaskMoreSheet(@NonNull Context context) {
        super(context);
    }

    public DownloadTaskMoreSheet(@NonNull Context context, ItemClickListener listener) {
        super(context);
        this.itemClickListener = listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_download_more;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
        List<MoreModel> modelList = new ArrayList<>();
        MoreModel deleteModel = new MoreModel("删除任务",R.mipmap.task_file_delete);
        MoreModel copyModel = new MoreModel("复制链接",R.mipmap.task_file_copy);
        MoreModel filePathModel = new MoreModel("文件路径",R.mipmap.task_file_path);
        MoreModel downloadModel = new MoreModel("下载文件",R.mipmap.task_file_more_download);

        modelList.add(downloadModel);
        modelList.add(deleteModel);
        modelList.add(copyModel);
        modelList.add(filePathModel);


        downloadMoreAdapter = new DownloadMoreAdapter(modelList);
        itemRv.setLayoutManager(gridLayoutManager);
        itemRv.setAdapter(downloadMoreAdapter);

        downloadMoreAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (itemClickListener != null) {
                itemClickListener.clickIndex(position);
                dismiss();
            }
        });

    }

    @OnClick(R.id.cancel_tv)
    public void cancelAction() {
        dismiss();
    }
}
