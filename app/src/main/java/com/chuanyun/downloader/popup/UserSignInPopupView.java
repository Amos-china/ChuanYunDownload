package com.chuanyun.downloader.popup;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserInfoModel;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.models.ApiRootModel;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import com.alibaba.fastjson.JSON;

public class UserSignInPopupView extends TTBaseCenterPopupview{

    @BindView(R.id.fen_tv)
    TextView fenTv;

    private int fen;
    private SigInPopupViewListener listener;
    private UserEngine userEngine;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public interface SigInPopupViewListener {
        void sigInButtonAction();
    }

    public UserSignInPopupView(@NonNull Context context,int fen, SigInPopupViewListener listener) {
        super(context);
        this.fen = fen;
        this.listener = listener;
        this.userEngine = new UserEngine(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_sign_in_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        fenTv.setText(String.valueOf(fen));
        
        // 请求积分接口更新用户积分
        refreshUserScore();
    }
    
    private void refreshUserScore() {
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        if (loginModel == null || loginModel.getToken() == null) {
            return;
        }
        
        Disposable disposable = userEngine.getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rootModel -> {
                    if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                        UserInfoModel userInfo = JSON.parseObject(rootModel.getData(), UserInfoModel.class);
                        if (userInfo != null) {
                            loginModel.setInfo(userInfo);
                            UserLoginManager.setLoginInfo(loginModel);
                            // 更新弹框中的积分显示
                            fenTv.setText(String.valueOf(userInfo.getFen()));
                        }
                    }
                }, throwable -> {
                    Log.e("UserSignInPopupView", "获取用户积分失败: " + throwable.getMessage());
                });
        compositeDisposable.add(disposable);
    }

    @OnClick(R.id.sign_in_tv)
    public void signInAction() {
        if (listener != null) {
            listener.sigInButtonAction();
        }
        dismiss();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
