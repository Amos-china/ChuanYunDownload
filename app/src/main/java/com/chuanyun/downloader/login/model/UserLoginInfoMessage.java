package com.chuanyun.downloader.login.model;

public class UserLoginInfoMessage {

    public static final String MMKV_ACCOUNT_PASSWORD = "MMKV_ACCOUNT_PASSWORD_KEY";

    private String account;
    private String password;
    private int remember = 0;

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRemember(int remember) {
        this.remember = remember;
    }

    public int getRemember() {
        return remember;
    }
}
