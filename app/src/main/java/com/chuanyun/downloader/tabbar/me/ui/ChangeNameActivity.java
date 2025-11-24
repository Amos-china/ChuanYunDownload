package com.chuanyun.downloader.tabbar.me.ui;

import android.text.TextUtils;
import android.widget.EditText;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.eventBusModel.ChangeUserInfoEvent;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChangeNameActivity extends BaseActivity {

    @BindView(R.id.name_et)
    EditText editText;

    private UserEngine userEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_name;
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        LoginModel loginModel = UserLoginManager.getLoginInfo();
        editText.setText(loginModel.getInfo().getName());
    }

    @Override
    protected void initData() {
        super.initData();

        userEngine = new UserEngine(this);
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }

    @OnClick(R.id.commit_tv)
    public void commitAction() {
        String name = editText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showToast("请输入昵称");
        }else {
            requestSetName(name);
        }
    }

    private void requestSetName(String name) {
        showDiaLog("",false);
        Disposable disposable = userEngine.modifyName(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringApiRootModel -> {
                    hideLoadingDialog();
                    showToast(stringApiRootModel.getMsg());
                    if (stringApiRootModel.getCode() == HttpConfig.STATUS_OK) {
                        LoginModel loginModel = UserLoginManager.getLoginInfo();
                        loginModel.getInfo().setName(name);
                        UserLoginManager.setLoginInfo(loginModel);
                        EventBus.getDefault().post(new ChangeUserInfoEvent());
                        finish();
                    }
                },throwable -> {
                    hideLoadingDialog();
                    showToast(throwable.getMessage());
                });
        addDisposable(disposable);
    }
}
