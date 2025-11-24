package com.chuanyun.downloader.tabbar.me.ui;

import android.widget.TextView;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.base.api.ApiEngine;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.utils.AppUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.version_tv)
    TextView versionTextView;

    private ApiEngine apiEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        versionTextView.setText(AppUtils.getVersionName(this));
    }

    @Override
    protected void initData() {
        super.initData();

        apiEngine = new ApiEngine(this);
    }

    @OnClick(R.id.update_tv)
    public void updateAction() {
        showDiaLog("",false);
        Disposable disposable = apiEngine.getApiIndex()
                .subscribe(rootModel -> {
                    hideLoadingDialog();
                    ApiIndexModel apiIndexModel = rootModel.getData();
                    if (apiIndexModel.getBbh() == AppUtils.getVersionCode(this)) {
//                        showToast("已经是最新版本");
                        showAlertView("提示信息","目前您的版本是非必须更新版本,点击确认可前往系统浏览器查看最新版本。",()-> {
                            openUrl("https://wwwz.lanzout.com/chuanyun");
                        });
                    }else {
                        showAppUpdateView(apiIndexModel);
                    }
                },throwable -> {
                    hideLoadingDialog();
                    showToast(throwable.getMessage());
                });
        addDisposable(disposable);
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }
}
