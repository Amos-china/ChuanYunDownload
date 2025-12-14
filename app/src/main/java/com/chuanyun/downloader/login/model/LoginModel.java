package com.chuanyun.downloader.login.model;

import com.alibaba.fastjson.annotation.JSONField;

public class LoginModel {

    public static final String MMKV_LOGIN_INFO = "MMKV_LOGIN_INFO_KEY";


    @JSONField(name = "token")
    private String token;
    @JSONField(name = "tokenState")
    private String tokenState;
    @JSONField(name = "info")
    private UserInfoModel info;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenState() {
        return tokenState;
    }

    public void setTokenState(String tokenState) {
        this.tokenState = tokenState;
    }

    public UserInfoModel getInfo() {
        return info;
    }

    public void setInfo(UserInfoModel info) {
        this.info = info;
    }

}
