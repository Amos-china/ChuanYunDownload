package com.chuanyun.downloader.bat;

import com.alibaba.fastjson.annotation.JSONField;

public class UmengConfigModel {
    private int id;
    @JSONField(name = "key_u_me")
    private String uMe;
    @JSONField(name = "key_u_ma")
    private String uMa;
    @JSONField(name = "t_key")
    private String tKey;
    @JSONField(name = "t_id")
    private String tId;
    private UmengConfigDataModel data;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getuMe() {
        return uMe;
    }

    public void setuMe(String uMe) {
        this.uMe = uMe;
    }

    public String getuMa() {
        return uMa;
    }

    public void setuMa(String uMa) {
        this.uMa = uMa;
    }

    public String gettKey() {
        return tKey;
    }

    public void settKey(String tKey) {
        this.tKey = tKey;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public UmengConfigDataModel getData() {
        return data;
    }

    public void setData(UmengConfigDataModel data) {
        this.data = data;
    }
}
