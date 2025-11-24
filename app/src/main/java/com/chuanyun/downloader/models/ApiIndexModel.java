package com.chuanyun.downloader.models;

public class ApiIndexModel {
    private String url;  // 主地址
    private int sftcgg;  // 是否弹出公告 0为不弹出 1为简单弹出 2为普通弹出
    private String ggbt;  // 公告标题
    private String ggnr;  // 公告内容
    private String jiandan;  // 简单确认按钮
    private String pudan;  // 普通确认按钮
    private String pudkurl;  // 普通按钮打开网址
    private long tancjg;  // 弹出公告时间间隔 单位 秒
    private long tanctzjg;  // 弹出通知时间间隔 单位 秒
    private int gxfs;  // 更新方式
    private String zlurl;  // 直链更新方式
    private String gxurl;  // 浏览器更新方式
    private String gxnr;  // 更新内容
    private int gxleixing;  // 更新类型 0为不更新 1为选择更新 2为必须更新
    private String gxbt;  // 更新标题
    private String gxfbt;  // 更新副标题
    private String fenxiang;  // 分享内容
    private String QQqun;  // QQ群号
    private String yhxieyi;  // 用户使用协议
    private String yszhengce;  // 隐私政策
    private String sfqqqun;  // 是否显示QQ交流群
    private String sfwxgzh;  // 是否显示微信公众号
    private String wxname;  // 公众号
    private String fcgfwz;  // 官网
    private long dttime;  // 当天时间戳
    private String vipPackage;  // 获取开通VIP套餐信息地址
    private String vipPackage2;  // 获取开通VIP套餐信息地址2
    private String gengxinxxurl;  // 更新信息获取地址
    private int bbh;  // 版本号
    private int sfyxfx;

    private int alipay;
    private int wxpay;

    private int sftcydsm;
    private String ydbt;
    private String ydnr;
    private String queding;

    private String jxynr;
    private String kaitong;
    private String jxbt;

    private String xzbt;
    private String xznr;
    private String tongyi;

    private String kfbt;
    private String kfqq;
    private String kfsm;
    private String fuzhi;

    private String jfxhsm;

    private String mail;

    private String mailinfo;

    private String zfbhqts;

    private String yqsrkts;
    private String yqsmwz;

    private String spzynr;
    private String spxznr;

    private String gfkf;

    public void setGfkf(String gfkf) {
        this.gfkf = gfkf;
    }

    public String getGfkf() {
        return gfkf;
    }

    public void setSpxznr(String spxznr) {
        this.spxznr = spxznr;
    }

    public String getSpxznr() {
        return spxznr;
    }

    public void setSpzynr(String spzynr) {
        this.spzynr = spzynr;
    }

    public String getSpzynr() {
        return spzynr;
    }

    public void setMailinfo(String mailinfo) {
        this.mailinfo = mailinfo;
    }

    public String getMailinfo() {
        return mailinfo;
    }

    // Getter and Setter methods for each property

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSftcgg() {
        return sftcgg;
    }

    public void setSftcgg(int sftcgg) {
        this.sftcgg = sftcgg;
    }

    public String getGgbt() {
        return ggbt;
    }

    public void setGgbt(String ggbt) {
        this.ggbt = ggbt;
    }

    public String getGgnr() {
        return ggnr;
    }

    public void setGgnr(String ggnr) {
        this.ggnr = ggnr;
    }

    public String getJiandan() {
        return jiandan;
    }

    public void setJiandan(String jiandan) {
        this.jiandan = jiandan;
    }

    public String getPudan() {
        return pudan;
    }

    public void setPudan(String pudan) {
        this.pudan = pudan;
    }

    public String getPudkurl() {
        return pudkurl;
    }

    public void setPudkurl(String pudkurl) {
        this.pudkurl = pudkurl;
    }

    public long getTancjg() {
        return tancjg;
    }

    public void setTancjg(long tancjg) {
        this.tancjg = tancjg;
    }

    public long getTanctzjg() {
        return tanctzjg;
    }

    public void setTanctzjg(long tanctzjg) {
        this.tanctzjg = tanctzjg;
    }

    public int getGxfs() {
        return gxfs;
    }

    public void setGxfs(int gxfs) {
        this.gxfs = gxfs;
    }

    public String getZlurl() {
        return zlurl;
    }

    public void setZlurl(String zlurl) {
        this.zlurl = zlurl;
    }

    public String getGxurl() {
        return gxurl;
    }

    public void setGxurl(String gxurl) {
        this.gxurl = gxurl;
    }

    public String getGxnr() {
        return gxnr;
    }

    public void setGxnr(String gxnr) {
        this.gxnr = gxnr;
    }

    public int getGxleixing() {
        return gxleixing;
    }

    public void setGxleixing(int gxleixing) {
        this.gxleixing = gxleixing;
    }

    public String getGxbt() {
        return gxbt;
    }

    public void setGxbt(String gxbt) {
        this.gxbt = gxbt;
    }

    public String getGxfbt() {
        return gxfbt;
    }

    public void setGxfbt(String gxfbt) {
        this.gxfbt = gxfbt;
    }

    public String getFenxiang() {
        return fenxiang;
    }

    public void setFenxiang(String fenxiang) {
        this.fenxiang = fenxiang;
    }

    public String getQQqun() {
        return QQqun;
    }

    public void setQQqun(String QQqun) {
        this.QQqun = QQqun;
    }

    public String getYhxieyi() {
        return yhxieyi;
    }

    public void setYhxieyi(String yhxieyi) {
        this.yhxieyi = yhxieyi;
    }

    public String getYszhengce() {
        return yszhengce;
    }

    public void setYszhengce(String yszhengce) {
        this.yszhengce = yszhengce;
    }

    public String getSfqqqun() {
        return sfqqqun;
    }

    public void setSfqqqun(String sfqqqun) {
        this.sfqqqun = sfqqqun;
    }

    public String getSfwxgzh() {
        return sfwxgzh;
    }

    public void setSfwxgzh(String sfwxgzh) {
        this.sfwxgzh = sfwxgzh;
    }

    public String getWxname() {
        return wxname;
    }

    public void setWxname(String wxname) {
        this.wxname = wxname;
    }

    public long getDttime() {
        return dttime;
    }

    public void setDttime(long dttime) {
        this.dttime = dttime;
    }

    public String getVipPackage() {
        return vipPackage;
    }

    public void setVipPackage(String vipPackage) {
        this.vipPackage = vipPackage;
    }

    public String getVipPackage2() {
        return vipPackage2;
    }

    public void setVipPackage2(String vipPackage2) {
        this.vipPackage2 = vipPackage2;
    }

    public String getGengxinxxurl() {
        return gengxinxxurl;
    }

    public void setGengxinxxurl(String gengxinxxurl) {
        this.gengxinxxurl = gengxinxxurl;
    }

    public int getBbh() {
        return bbh;
    }

    public void setBbh(int bbh) {
        this.bbh = bbh;
    }

    public void setSfyxfx(int sfyxfx) {
        this.sfyxfx = sfyxfx;
    }

    public int getSfyxfx() {
        return sfyxfx;
    }

    public void setAlipay(int alipay) {
        this.alipay = alipay;
    }

    public int getAlipay() {
        return alipay;
    }

    public void setWxpay(int wxpay) {
        this.wxpay = wxpay;
    }

    public int getWxpay() {
        return wxpay;
    }

    public void setSftcydsm(int sftcydsm) {
        this.sftcydsm = sftcydsm;
    }

    public int getSftcydsm() {
        return sftcydsm;
    }

    public void setYdbt(String ydbt) {
        this.ydbt = ydbt;
    }

    public String getYdbt() {
        return ydbt;
    }

    public void setYdnr(String ydnr) {
        this.ydnr = ydnr;
    }

    public String getYdnr() {
        return ydnr;
    }

    public void setQueding(String queding) {
        this.queding = queding;
    }

    public String getQueding() {
        return queding;
    }

    public void setKaitong(String kaitong) {
        this.kaitong = kaitong;
    }

    public String getKaitong() {
        return kaitong;
    }

    public void setJxynr(String jxynr) {
        this.jxynr = jxynr;
    }

    public String getJxynr() {
        return jxynr;
    }

    public void setJxbt(String jxbt) {
        this.jxbt = jxbt;
    }

    public String getJxbt() {
        return jxbt;
    }


    public void setXzbt(String xzbt) {
        this.xzbt = xzbt;
    }

    public String getXzbt() {
        return xzbt;
    }

    public void setXznr(String xznr) {
        this.xznr = xznr;
    }

    public String getXznr() {
        return xznr;
    }

    public void setTongyi(String tongyi) {
        this.tongyi = tongyi;
    }

    public String getTongyi() {
        return tongyi;
    }

    public String getKfbt() {
        return kfbt;
    }

    public void setKfbt(String kfbt) {
        this.kfbt = kfbt;
    }

    public String getKfqq() {
        return kfqq;
    }

    public void setKfqq(String kfqq) {
        this.kfqq = kfqq;
    }

    public String getKfsm() {
        return kfsm;
    }

    public void setKfsm(String kfsm) {
        this.kfsm = kfsm;
    }

    public String getFuzhi() {
        return fuzhi;
    }

    public void setFuzhi(String fuzhi) {
        this.fuzhi = fuzhi;
    }

    public void setJfxhsm(String jfxhsm) {
        this.jfxhsm = jfxhsm;
    }

    public String getJfxhsm() {
        return jfxhsm;
    }

    public void setFcgfwz(String fcgfwz) {
        this.fcgfwz = fcgfwz;
    }

    public String getFcgfwz() {
        return fcgfwz;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public String[] getCanRegisterMailList() {
        return mail.split("-");
    }


    public void setZfbhqts(String zfbhqts) {
        this.zfbhqts = zfbhqts;
    }

    public String getZfbhqts() {
        return zfbhqts;
    }

    public void setYqsmwz(String yqsmwz) {
        this.yqsmwz = yqsmwz;
    }

    public String getYqsmwz() {
        return yqsmwz;
    }

    public void setYqsrkts(String yqsrkts) {
        this.yqsrkts = yqsrkts;
    }

    public String getYqsrkts() {
        return yqsrkts;
    }
}
