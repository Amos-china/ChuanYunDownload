package com.chuanyun.downloader.tabbar.me.ui;

import android.text.TextUtils;
import android.widget.EditText;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.UserLoginManager;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.edit_password)
    EditText passwordEt;

    @BindView(R.id.edit_new_password)
    EditText newPasswordEt;

    private UserEngine userEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void initData() {
        super.initData();

        userEngine = new UserEngine(this);

    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }

    @OnClick(R.id.commit_tv)
    public void commitAction() {
        String password = passwordEt.getText().toString().trim();
        String newPassword = newPasswordEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            showToast("请输入旧密码");
        }else {
            if (TextUtils.isEmpty(newPassword)) {
                showToast("请输入新密码");
            }else {
                requestChangePassword(password,newPassword);
            }
        }
    }

    private void requestChangePassword(String password,String newPassword) {
        showDiaLog("",false);
        Disposable disposable = userEngine.modifyPwd(password,newPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringApiRootModel -> {
                    hideLoadingDialog();
                    showToast(stringApiRootModel.getMsg());
                    if (stringApiRootModel.getCode() == HttpConfig.STATUS_OK) {
                        UserLoginManager.userMessageChangePassword(newPassword);
                        finish();
                    }
                },throwable -> {
                    hideLoadingDialog();
                    showToast(throwable.getMessage());
                });
        addDisposable(disposable);

    }
}
