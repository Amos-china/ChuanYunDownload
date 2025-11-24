package com.chuanyun.downloader.login.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.chuanyun.downloader.app.App;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class UserInfoModel {
    @JSONField(name = "uid")
    private String uid;
    @JSONField(name = "phone")
    private String phone;
    @JSONField(name = "email")
    private String email;
    @JSONField(name = "acctno")
    private String acctno;
    @JSONField(name = "name")
    private String name;
    @JSONField(name = "pic")
    private String pic;
    @JSONField(name = "invID")
    private String invID;
    @JSONField(name = "fen")
    private int fen;
    @JSONField(name = "vipExpTime")
    private long vipExpTime;
    @JSONField(name = "vipExpDate")
    private String vipExpDate;
    @JSONField(name = "agent")
    private int agent;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAcctno() {
        return acctno;
    }

    public void setAcctno(String acctno) {
        this.acctno = acctno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getInvID() {
        return invID;
    }

    public void setInvID(String invID) {
        this.invID = invID;
    }

    public int getFen() {
        return fen;
    }

    public void setFen(int fen) {
        this.fen = fen;
    }

    public long getVipExpTime() {
        return vipExpTime;
    }

    public void setVipExpTime(long vipExpTime) {
        this.vipExpTime = vipExpTime;
    }

    public String getVipExpDate() {
        return vipExpDate;
    }

    public void setVipExpDate(String vipExpDate) {
        this.vipExpDate = vipExpDate;
    }

    public int getAgent() {
        return agent;
    }

    public void setAgent(int agent) {
        this.agent = agent;
    }

    public String getUserVipStr() {
        if (vipExpTime == 0) {
            return "普通用户";
        }else if (vipExpTime > App.getApp().getCloudTime()) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(vipExpTime * 1000), ZoneId.systemDefault());
            LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(App.getApp().getCloudTime() * 1000), ZoneId.systemDefault());
            int year = dateTime.getYear();
            int currentYear = currentTime.getYear();
            if (year - currentYear > 100) {
                return "会员有效期:永久" ;
            } else {
                return "会员有效期:" + vipExpDate;
            }

        }else {
            return "会员已到期:" + vipExpDate;
        }
    }

    public int getVipStatus() {
        if (vipExpTime == 0) {
            return 0;
        }else if (vipExpTime > App.getApp().getCloudTime()) {
            return 1;
        }else {
            return 2;
        }
    }
}
