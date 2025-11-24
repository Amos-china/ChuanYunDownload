package com.chuanyun.downloader.tabbar.me.ui;

import android.text.TextUtils;
import android.view.View;
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

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SetAcctnoActivity extends BaseActivity {

    @BindView(R.id.nike_name_tv)
    TextView nikeNameTv;

    @BindView(R.id.title_tv)
    TextView titleTv;

    @BindView(R.id.name_et)
    EditText editText;

    @BindView(R.id.ts_tv)
    TextView tsTv;

    private UserEngine userEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_name;
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

        LoginModel loginModel = UserLoginManager.getLoginInfo();
        if (!TextUtils.isEmpty(loginModel.getInfo().getAcctno())) {
            editText.setText(loginModel.getInfo().getAcctno());
        }

        editText.setHint("请输入账号");
        nikeNameTv.setText("账号：");
        titleTv.setText("设置账号");

        tsTv.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.commit_tv)
    public void commitAction() {
        String acctno = editText.getText().toString().toString();
        if (TextUtils.isEmpty(acctno)) {
            showToast("请输入账户");
        }else {
            requestSetAcctno(acctno);
        }
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }

    private void requestSetAcctno(String acctno) {
        showDiaLog("",false);
        Disposable disposable = userEngine.setAcctno(acctno)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringApiRootModel -> {
                    hideLoadingDialog();
                    showToast(stringApiRootModel.getMsg());
                    if (stringApiRootModel.getCode() == HttpConfig.STATUS_OK) {
                        LoginModel loginModel = UserLoginManager.getLoginInfo();
                        loginModel.getInfo().setAcctno(acctno);
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
