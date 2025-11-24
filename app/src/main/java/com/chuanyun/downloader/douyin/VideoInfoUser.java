package com.chuanyun.downloader.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class VideoInfoUser {
    @JSONField(name = "nike_name")
    private String nikeName;
    @JSONField(name = "unique_id")
    private String uniqueId;
    @JSONField(name = "sec_uid")
    private String secUid;
    @JSONField(name = "short_id")
    private String shortId;
    private String signature;
    private String avatar;

    public String getNikeName() {
        return nikeName;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSecUid() {
        return secUid;
    }

    public void setSecUid(String secUid) {
        this.secUid = secUid;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
