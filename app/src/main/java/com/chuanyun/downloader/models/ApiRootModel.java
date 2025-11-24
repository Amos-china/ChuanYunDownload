package com.chuanyun.downloader.models;

public class ApiRootModel<T> {
    private int code;
    private String msg;
    private T data;
    private long time = 0;

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
