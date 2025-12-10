package com.chuanyun.downloader.launcher;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.chuanyun.downloader.core.TTDownloadService;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginInfoMessage;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.popup.AppPrivacyPopupView;
import com.chuanyun.downloader.popup.ShowUserContentPopupView;
import com.chuanyun.downloader.tabbar.MainActivity;
import com.chuanyun.downloader.utils.MMKVUtils;
import com.chuanyun.downloader.utils.NetworkUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LauncherActivity extends BaseActivity {

    private UserEngine userEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void initData() {
        super.initData();

        userEngine = new UserEngine(this);
    }

    @Override
    protected void initView() {
        super.initView();

        requestPermissions();

        checkReadPrivacy();
    }

    private void checkReadPrivacy() {
        if (MMKVUtils.getIsReadPrivacy()) {
            checkNetWork();
        }else {
            showAppPrivacyView();
        }
    }

    private void showAppPrivacyView() {
        AppPrivacyPopupView privacyPopupView = new AppPrivacyPopupView(this, new AppPrivacyPopupView.OnButtonClickListener() {
            @Override
            public void cancel() {
                finish();
            }

            @Override
            public void next() {
                MMKVUtils.setReadPrivacy();
                checkNetWork();
            }
        });

        showCustomPopupView(privacyPopupView,false);
    }

    private void checkNetWork() {
        if (NetworkUtils.isNetworkConnected(this)) {
            getIndex();
        }else {
            showAlertView("请在连接网络的情况下使用App","退出","网络已连接",this::checkNetWork,this::finish);
        }
    }

    private int errorCount = 1;

    private void getIndex() {
        Disposable disposable = apiEngine.getApiIndex().retry(3).subscribe(rootModel -> {
           if (rootModel.getCode() == HttpConfig.STATUS_OK) {
               ApiIndexModel indexModel = rootModel.getData();
               App.getApp().setApiIndexModel(indexModel);
               if (checkIndexModel(indexModel)) {
                   if (checkAppVersion(indexModel)) {
                       startMainController();
//                       loginUser();
                   }
               }
           }else {
               showAlertView("连接错误","code:" + rootModel.getCode() + "\n" + "msg:" + rootModel.getMsg(),this::getIndex);
           }
        },throwable -> {
            errorCount ++;
            if (errorCount < 4) {
                if (errorCount == 3) {
                    App.getApp().setRequestApi("http://154.44.24.202/");
                    getIndex();
                }else {
                    String api = "https://api-chuanyun-" + errorCount + ".kuaiyunpan.cc/";
                    App.getApp().setRequestApi(api);
                    getIndex();
                }
            }else {
                String msg = "服务器访问失败，可能是你的网络出现问题，请尝试切换网络，如果依旧无法连接服务器，或者访问官网下载最新版软件";
                showAlertView("提示信息",msg,"取消","官方网站",()-> {},()->{
                    openUrl("https://chuanyun-www.kuaiyunpan.cc");
                    finish();
                });
            }
        });
        addDisposable(disposable);
    }

    private void startMainController() {
        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestPermissions() {
        XXPermissions.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            Log.i(TAG, "获取部分权限成功，但部分权限未正常授予");
                            return;
                        }
                        Log.i(TAG,"获取文件读写权限成功");

                        App.getApp().createDataBase();

                        TTDownloadService.deleteTempFolder();

                        TTDownloadService.getInstance().initXL(App.getApp());
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        ShowUserContentPopupView showUserContentPopupView = new ShowUserContentPopupView(
                                LauncherActivity.this,
                                "权限请求",
                                getResources().getString(R.string.permissions_message),
                                "请求权限",
                                "取消",
                                index -> {
                                    if (index == 1) {
                                        if (doNotAskAgain) {
                                            XXPermissions.startPermissionActivity(LauncherActivity.this, permissions);
                                            finish();
                                        }else {
                                            requestPermissions();
                                        }
                                    }else {
                                        finish();
                                    }
                                });
                        showBottomPopupView(showUserContentPopupView,false);
                    }
                });
    }

}
