package com.chuanyun.downloader.popup;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.models.TorrentFileInfoModel;
import com.chuanyun.downloader.utils.ClipboardHelper;
import com.chuanyun.downloader.utils.FileTypeUtils;
import com.chuanyun.downloader.utils.StorageHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class ShowFilePathPopupView extends TTBaseCenterPopupview {


    @BindView(R.id.file_type_im)
    ImageView typeIm;

    @BindView(R.id.name_tv)
    TextView nameTv;

    @BindView(R.id.show_path_stv)
    SuperTextView showPathStv;

    @BindView(R.id.tag_tv)
    TextView tagTv;

    private TorrentFileInfoModel torrentFileInfoModel;


    public ShowFilePathPopupView(Context context, TorrentFileInfoModel fileInfoModel) {
        super(context);
        this.torrentFileInfoModel = fileInfoModel;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_show_file_path_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        typeIm.setImageResource(FileTypeUtils.getFileTypeRes(torrentFileInfoModel));
        nameTv.setText(torrentFileInfoModel.getName());
        showPathStv.setText(torrentFileInfoModel.getFilePath() + torrentFileInfoModel.getName());

        boolean check = StorageHelper.doesPathExist(torrentFileInfoModel.getFilePath() + torrentFileInfoModel.getName());

        tagTv.setVisibility(check ? GONE : VISIBLE);

    }

    @OnClick(R.id.copy_name_stv)
    public void copyNameAction() {
        ClipboardHelper.copyTextToClipboard(getContext(),torrentFileInfoModel.getName());
        showToast("复制成功");
        dismiss();
    }

    @OnClick(R.id.copy_path_stv)
    public void copyPathAction() {
        ClipboardHelper.copyTextToClipboard(getContext(),torrentFileInfoModel.getFilePath() + torrentFileInfoModel.getName());
        showToast("复制成功");
        dismiss();
    }

}
