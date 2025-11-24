package com.chuanyun.downloader.app;


import com.tencent.bugly.crashreport.CrashReport;
import com.tools.aplayer.PlayerApp;
import com.chuanyun.downloader.config.Constant;
import com.chuanyun.downloader.dao.AppDataBase;
import com.chuanyun.downloader.httpService.RetrofitHttpRequest;



public class App extends BaseApp {

    private static App app;
    public static App getApp() {return app;}

    private AppDataBase appDataBase;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        initHttp();


        PlayerApp.initVApplication(this);

    }

    public void createDataBase() {
        if (appDataBase == null) {
            appDataBase = AppDataBase.createDataBase(this);
        }
    }

    public AppDataBase getAppDataBase() {
        return appDataBase;
    }

    public void initHttp() {
        new RetrofitHttpRequest.Builder(Constant.BASE_URL);
    }
}
