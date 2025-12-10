package com.chuanyun.downloader.login.engine;

import android.content.Context;
import android.text.TextUtils;

import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.httpService.BaseEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.models.ApiRootModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserEngine extends BaseEngine {

    private String token;

    public UserEngine(Context context) {
        super(context);
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        if (loginModel != null) {
            token = UserLoginManager.getLoginInfo().getToken();
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    //注册=0、重置密码=1、绑定账号=2、换绑机器码=3
    private String getCodeType(int type) {
        if (type == 0) {
            return "reg";
        }else if (type == 1) {
            return "repwd";
        }else if (type == 2) {
            return "ubind";
        }else {
            return "resn";
        }
    }


    public Observable<ApiRootModel<String>> getCode(String account,int type) {
        return request.getCode(account,getCodeType(type));
    }

    public Observable<ApiRootModel<String>> userReg(String account, String code, String password, String udid) {
        return request.userReg(account, code, password, udid);
    }

    public Observable<ApiRootModel<String>> userLogin(String account, String password, String udid) {
        return request.userLogin(account, password, udid);
    }

    public Observable<ApiRootModel<String>> loginOut() {
        return request.loginOut(token);
    }

    public Observable<ApiRootModel<String>> setEmail(String email,String code) {
        return request.setEmail(token,email,code);
    }

    public Observable<ApiRootModel<String>> modifyName(String name) {
        return request.modifyName(token,name);
    }

    public Observable<ApiRootModel<String>> modifyPwd(String password, String newPassword) {
        return request.modifyPwd(token, password,newPassword);
    }

    public Observable<ApiRootModel<String>> resetPwd(String account, String code, String password) {
        return request.resetPwd(account, code, password);
    }

    public Observable<ApiRootModel<String>> setAcctno(String acctno) {
        return request.setAcctno(token, acctno);
    }

    public Observable<ApiRootModel<String>> checkVip() {
        return request.checkVip(token);
    }

    public Observable<ApiRootModel<String>> getUserInfo() {
        return request.getUserInfo(token);
    }

    public Observable<ApiRootModel<String>> heartbeat() {
        return request.heartbeat(token);
    }

    public Observable<ApiRootModel<String>> getGoodsList() {
        return request.getGoods(token);
    }

    public Observable<ApiRootModel<String>> fen(int fen) {
        return request.fen(token,fen);
    }

    public Observable<ApiRootModel<String>> userSignIn() {
        return request.userSignIn(token);
    }

    public Observable<ApiRootModel<String>> pay(String gid,int payType) {
        String type = payType == 0 ? "ali" : "wx";
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        return request.pay(loginModel.getInfo().getEmail(),gid,type);
    }

    public Disposable ping() {
        return Observable.interval(20, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .filter(aLong -> UserLoginManager.checkUserLogin())
                .flatMap(aLong -> heartbeat())
                .retry()
                .subscribe(rootModel -> {
                    if (rootModel.getTime() != 0) {
                        App.getApp().setCloudTime(rootModel.getTime());
                    }
                }, Throwable::printStackTrace);
    }
}
