package com.chuanyun.downloader.popup;

import android.content.Context;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.models.ApiIndexModel;

import butterknife.BindView;
import butterknife.OnClick;

public class AppUpdatePopupView extends TTBaseCenterPopupview {

    @BindView(R.id.size_tv)
    TextView sizeTv;

    @BindView(R.id.version_tv)
    TextView versionTv;

    @BindView(R.id.content_tv)
    TextView contentTv;

    @BindView(R.id.update_stv)
    SuperTextView updateStv;

    @BindView(R.id.wp_update_stv)
    SuperTextView wpUpdateStv;

    @BindView(R.id.np_bar)
    NumberProgressBar progressBar;

    private ApiIndexModel indexModel;

    public interface UpdateButtonListener {
        void updateButtonAction(int type);
    }

    private UpdateButtonListener updateButtonListener;

    public AppUpdatePopupView(Context context, ApiIndexModel indexModel,UpdateButtonListener listener) {
        super(context);
        this.indexModel = indexModel;
        this.updateButtonListener = listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_app_update;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        versionTv.setText(indexModel.getGxbt());

        sizeTv.setText(indexModel.getGxfbt());

        contentTv.setText(indexModel.getGxnr());

        progressBar.setVisibility(GONE);

        if (indexModel.getGxfs() == 1) {
            wpUpdateStv.setVisibility(VISIBLE);
            updateStv.setVisibility(GONE);
        }else if (indexModel.getGxfs() == 2) {
            wpUpdateStv.setVisibility(GONE);
            updateStv.setVisibility(VISIBLE);
        }else {
            wpUpdateStv.setVisibility(VISIBLE);
            updateStv.setVisibility(VISIBLE);
        }

    }

    @OnClick(R.id.update_stv)
    public void updateVersion() {
        updateStv.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        wpUpdateStv.setVisibility(GONE);
        if (updateButtonListener != null) {
            updateButtonListener.updateButtonAction(0);
        }
    }

    @OnClick(R.id.wp_update_stv)
    public void wpUpdateVersion() {
        if (updateButtonListener != null) {
            updateButtonListener.updateButtonAction(1);
        }
    }

    public void uploadProgress(int max, int progress) {
        int p =  (int) ((long)(progress * 100L) / max);
        progressBar.setProgress(p);
    }
}
