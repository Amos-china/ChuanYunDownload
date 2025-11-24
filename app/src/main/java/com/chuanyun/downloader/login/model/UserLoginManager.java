package com.chuanyun.downloader.login.model;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tencent.mmkv.MMKV;

public class UserLoginManager {

    public static void setLoginInfo(LoginModel loginInfo) {
        String loginJson = JSON.toJSONString(loginInfo);
        MMKV.defaultMMKV().putString(LoginModel.MMKV_LOGIN_INFO,loginJson);
    }

    public static LoginModel getLoginInfo() {
        String json = MMKV.defaultMMKV().getString(LoginModel.MMKV_LOGIN_INFO,"");
        if (TextUtils.isEmpty(json)) {
            return null;
        }else {
            return JSON.parseObject(json,LoginModel.class);
        }
    }

    public static void clearLoginInfo() {
        MMKV.defaultMMKV().putString(LoginModel.MMKV_LOGIN_INFO,"");
    }

    public static void setUserMessage(UserLoginInfoMessage loginInfoMessage) {
        String message = JSON.toJSONString(loginInfoMessage);
        MMKV.defaultMMKV().putString(UserLoginInfoMessage.MMKV_ACCOUNT_PASSWORD,message);
    }

    public static void updateUserMessage(String account,String password,int remember) {
        UserLoginInfoMessage userLoginInfoMessage = new UserLoginInfoMessage();
        userLoginInfoMessage.setPassword(password);
        userLoginInfoMessage.setAccount(account);
        userLoginInfoMessage.setRemember(remember);
        setUserMessage(userLoginInfoMessage);
    }

    public static void userMessageChangePassword(String newPassword) {
        UserLoginInfoMessage message = getUserLoginMessage();
        message.setPassword(newPassword);
        setUserMessage(message);
    }

    public static UserLoginInfoMessage getUserLoginMessage() {
        String json = MMKV.defaultMMKV().getString(UserLoginInfoMessage.MMKV_ACCOUNT_PASSWORD,"");
        if (TextUtils.isEmpty(json)) {
            return null;
        }else {
            return JSON.parseObject(json,UserLoginInfoMessage.class);
        }
    }

    public static boolean checkUserLogin() {
        return getLoginInfo() != null;
    }

    private static final String NOTICE_SHOW_TIME_KEY = "NOTICE_SHOW_TIME_KEY";
    public static void setNoticeShowTime(long time) {
        MMKV.defaultMMKV().putLong(NOTICE_SHOW_TIME_KEY,time);
    }

    public static long getNoticeShowTime() {
        long time = MMKV.defaultMMKV().getLong(NOTICE_SHOW_TIME_KEY,0);
        return time;
    }
}
