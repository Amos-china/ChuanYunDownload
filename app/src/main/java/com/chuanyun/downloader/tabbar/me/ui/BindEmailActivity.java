package com.chuanyun.downloader.tabbar.me.ui;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.eventBusModel.ChangeUserInfoEvent;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginManager;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BindEmailActivity extends BaseActivity {

    private UserEngine userEngine;

    @BindView(R.id.edit_email)
    EditText emailEt;

    @BindView(R.id.edit_code)
    EditText codeEt;

    @BindView(R.id.get_code_tv)
    TextView getCodeTv;

    private boolean canGetCode = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_email;
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

    @OnClick(R.id.commit_tv)
    public void commitAction() {
        String email = emailEt.getText().toString().trim();
        String code = codeEt.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            showToast("请输入邮箱");
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (TextUtils.isEmpty(code)) {
                    showToast("请输入验证码");
                }else {
                    requestBindEmail(email,code);
                }
            }else {
                showToast("请输入正确的邮箱");
            }
        }
    }

    @OnClick(R.id.get_code_tv)
    public void getCodeAction() {
        if (canGetCode == false) {
            return;
        }
        String email = emailEt.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            showToast("请输入邮箱");
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                requestGetCode(email);
            }else {
                showToast("请输入正确的邮箱");
            }
        }
    }

    private void requestBindEmail(String email,String code) {
        showDiaLog("",false);
        Disposable disposable = userEngine.setEmail(email, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringApiRootModel -> {
                    hideLoadingDialog();
                    showToast(stringApiRootModel.getMsg());
                    if (stringApiRootModel.getCode() == HttpConfig.STATUS_OK) {
                        LoginModel loginModel = UserLoginManager.getLoginInfo();
                        loginModel.getInfo().setEmail(email);
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

    private void requestGetCode(String email) {
        showDiaLog("",false);
       Disposable disposable = userEngine.getCode(email,2)
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
                    }
                    getCodeTv.setText(300 - aLong + "秒后重新发送");
                    canGetCode = false;
                },throwable -> {
                    hideLoadingDialog();
                },() -> {
                    canGetCode = true;
                });
       addDisposable(disposable);
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }
}
