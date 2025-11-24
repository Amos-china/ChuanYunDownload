package com.chuanyun.downloader.tabbar.me.ui;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginManager;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RestPasswordActivity extends BaseActivity {

    @BindView(R.id.email_tv)
    EditText emailEt;

    @BindView(R.id.edit_code)
    EditText codeEt;

    @BindView(R.id.get_code_tv)
    TextView getCodeTv;

    @BindView(R.id.edit_password)
    EditText passwordEdit;

    private UserEngine userEngine;

    private LoginModel loginModel;

    private boolean canGetCode = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected void initData() {
        super.initData();

        userEngine = new UserEngine(this);
        loginModel = UserLoginManager.getLoginInfo();
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        if (loginModel != null) {
            emailEt.setText(loginModel.getInfo().getEmail());
            emailEt.setFocusable(false);
            emailEt.setFocusableInTouchMode(false);
        }
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }

    @OnClick(R.id.get_code_tv)
    public void getCodeAction() {
        if (canGetCode) {
            String email = emailEt.getText().toString().trim();
            requestGetCode(email);
        }
    }

    @OnClick(R.id.commit_tv)
    public void commitAction() {
        String code = codeEt.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            showToast("请输入验证码");
        }else {
            if (TextUtils.isEmpty(password)) {
                showToast("请输入密码");
            }else {
                String account = emailEt.getText().toString().trim();
                requestResetPassword(account,code,password);
            }
        }
    }

    private void requestGetCode(String email) {
        showDiaLog("",false);
        Disposable disposable = userEngine.getCode(email,1)
                .subscribeOn(Schedulers.io())
                .flatMap(stringApiRootModel -> {
                    if (stringApiRootModel.getCode() == HttpConfig.STATUS_OK) {
                        return Observable.interval(1, TimeUnit.SECONDS).take(300);
                    }else {
                        return Observable.error(new Throwable(stringApiRootModel.getMsg()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong == 0) {
                        hideLoadingDialog();
                        showToast("发送成功");
                    }
                    getCodeTv.setText((300 - aLong) + "秒后重新发送");
                    canGetCode = false;
                },throwable -> {
                    hideLoadingDialog();
                    canGetCode = true;
                    showToast(throwable.getMessage());
                },() -> {
                    getCodeTv.setText("发送验证码");
                    canGetCode = true;
                });
        addDisposable(disposable);
    }

    private void requestResetPassword(String account, String code, String password) {
        showDiaLog("",false);
        Disposable disposable = userEngine.resetPwd(account, code, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringApiRootModel -> {
                    hideLoadingDialog();
                    showToast(stringApiRootModel.getMsg());
                    if (stringApiRootModel.getCode() == HttpConfig.STATUS_OK) {
                        UserLoginManager.userMessageChangePassword(password);
                        finish();
                    }
                },throwable -> {
                    hideLoadingDialog();
                    showToast(throwable.getMessage());
                });
        addDisposable(disposable);
    }
}
