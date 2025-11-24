package com.chuanyun.downloader.tabbar.me.ui;

import android.content.Intent;
import android.text.TextUtils;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.base.view.CustomItemLinearLayout;
import com.chuanyun.downloader.eventBusModel.ChangeUserInfoEvent;
import com.chuanyun.downloader.eventBusModel.LoginOutEvent;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserCenterActivity extends BaseActivity {

    @BindView(R.id.name_cll)
    CustomItemLinearLayout nameLL;

    private UserEngine userEngine;

    @BindView(R.id.set_acctno_cll)
    CustomItemLinearLayout acctnoCll;

    @BindView(R.id.bind_email_cll)
    CustomItemLinearLayout bindEmailCll;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initData() {
        super.initData();

        userEngine = new UserEngine(this);

        registerEventBus();
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        configView();
    }

    private void configView() {
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        nameLL.setSubTitleText(loginModel.getInfo().getName());
        bindEmailCll.setSubTitleText(loginModel.getInfo().getEmail());
        String acctno = TextUtils.isEmpty(loginModel.getInfo().getAcctno()) ? "" : loginModel.getInfo().getAcctno();
        acctnoCll.setSubTitleText(acctno);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeUserEvent(ChangeUserInfoEvent changeUserInfoEvent) {
        configView();
    }

    @OnClick(R.id.name_cll)
    public void changeNameAction() {
        Intent intent = new Intent(this,ChangeNameActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.chang_password_cll)
    public void changePasswordAction() {
        Intent intent = new Intent(this,ChangePasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.set_acctno_cll)
    public void setAcctnoAction() {
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        if (TextUtils.isEmpty(loginModel.getInfo().getAcctno())) {
            Intent intent = new Intent(this,SetAcctnoActivity.class);
            startActivity(intent);
        }else {
            showToast("账户一旦修改成功后不能再次修改");
        }
    }

    @OnClick(R.id.reset_password_cll)
    public void resetPasswordAction() {
        Intent intent = new Intent(this,RestPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.bind_email_cll)
    public void bindEmailAction() {
        Intent intent = new Intent(this, BindEmailActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.loginOut_tv)
    public void loginOutAction() {
        showAlertView("提示信息","是否退出当前账号登录?",this::requestLoginOut);
    }

    private void requestLoginOut() {
        showDiaLog("",false);
        Disposable disposable = userEngine.loginOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringApiRootModel -> {
                    hideLoadingDialog();
                    showToast(stringApiRootModel.getMsg());
                    if (stringApiRootModel.getCode() == HttpConfig.STATUS_OK) {
                        UserLoginManager.clearLoginInfo();
                        EventBus.getDefault().post(new LoginOutEvent());
                        finish();
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
