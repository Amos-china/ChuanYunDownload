package com.chuanyun.downloader.tabbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentPagerAdapter;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.UriUtils;
import com.chuanyun.downloader.eventBusModel.ShowAddTorrentViewEvent;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.umeng.analytics.MobclickAgent;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.base.view.ControlScrollViewPager;
import com.chuanyun.downloader.base.view.DraggableImageView;
import com.chuanyun.downloader.core.TTDownloadService;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginInfoMessage;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.models.TTTorrentInfo;
import com.chuanyun.downloader.popup.ShowUserContentPopupView;
import com.chuanyun.downloader.tabbar.adapter.MainPagerAdapter;
import com.chuanyun.downloader.tabbar.adapter.TabBarAdapter;
import com.chuanyun.downloader.tabbar.home.ui.TorrentDetailActivity;
import com.chuanyun.downloader.tabbar.model.TabBarItemModel;
import com.chuanyun.downloader.utils.ClipboardHelper;
import com.chuanyun.downloader.utils.MMKVUtils;
import com.chuanyun.downloader.utils.StorageHelper;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private List<TabBarItemModel> tabBarItemModelList = new ArrayList<>();
//    private List<Fragment> fragmentList = new ArrayList<>();

    @BindView(R.id.tabBar_view)
    MagicIndicator magicIndicator;

    @BindView(R.id.view_pager)
    ControlScrollViewPager viewPager;

    MainPagerAdapter pagerAdapter;


    private boolean isRequest = false;

    private UserEngine userEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();

        TabBarItemModel kaoshiItem = new TabBarItemModel("浏览器",R.mipmap.home_o_xx,R.mipmap.home_xx);
        TabBarItemModel lianxiItem = new TabBarItemModel("文件",R.mipmap.folder_n,R.mipmap.folder_s);
        TabBarItemModel downloadItem = new TabBarItemModel("任务",R.mipmap.download_n,R.mipmap.download_s);
        TabBarItemModel tiktokItem = new TabBarItemModel("视频",R.mipmap.tabbar_tiktok_n,R.mipmap.tabbar_tiktok_s);
        TabBarItemModel meItem = new TabBarItemModel("我的",R.mipmap.user_o_xx,R.mipmap.user_xx);

        tabBarItemModelList.add(downloadItem);
        tabBarItemModelList.add(lianxiItem);
        tabBarItemModelList.add(kaoshiItem);
        tabBarItemModelList.add(tiktokItem);
        tabBarItemModelList.add(meItem);



//        fragmentList.add(new HomeIndexFragment());
//        fragmentList.add(new TorrentIndexFragment());
//        fragmentList.add(new DownloadIndexFragment());
//        fragmentList.add(new MeIndexFragment());

        userEngine = new UserEngine(this);
        Disposable disposable = userEngine.ping();
        addDisposable(disposable);

    }

    @Override
    protected void initView() {
        super.initView();

        initViewPager();

        initTabBar();

        requestPermissions();

        Intent getIntent = getIntent();
        checkIndex(getIntent);

        registerEventBus();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        checkIndex(intent);
    }

    private void checkIndex(Intent intent) {
        if (App.getApp().getApiIndexModel() == null) {
            showDiaLog("",false);
            Disposable disposable = apiEngine.getApiIndex()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(rootModel -> {
                hideLoadingDialog();
                if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                    ApiIndexModel indexModel = rootModel.getData();
                    App.getApp().setApiIndexModel(indexModel);
                    if (checkIndexModel(indexModel)) {
                        if (checkAppVersion(indexModel)) {
                            loginUser(intent);
                        }
                    }
                }else {
                    showAlertView("提示信息",rootModel.getMsg(),() -> {checkIndex(intent);});
                }
            });
            addDisposable(disposable);
        }else {
            openTorrentFile(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowAddTorrentView(ShowAddTorrentViewEvent event) {
        showAddTorrentPopup("");
    }


    private void loginUser(Intent intent) {
        if (UserLoginManager.checkUserLogin()) {
            UserLoginInfoMessage message = UserLoginManager.getUserLoginMessage();
            Disposable disposable = userEngine.userLogin(message.getAccount(),message.getPassword(),App.getApp().getDeviceUuid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(rootModel -> {
                        if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                            LoginModel loginModel = JSON.parseObject(rootModel.getData(),LoginModel.class);
                            UserLoginManager.setLoginInfo(loginModel);
                            openTorrentFile(intent);
                        }else  {
                            showAlertView("连接错误","code:" + rootModel.getCode() + "\n" + "msg:" + rootModel.getMsg(),()->{
                                checkIndex(intent);
                            });
                        }
                    }, Throwable::printStackTrace);
            addDisposable(disposable);
        }else {
            openTorrentFile(intent);
        }
    }

    private void openTorrentFile(Intent intent) {
        if (!MMKVUtils.getIsReadPrivacy()) {
            showAlertView("第一次使用请先手动点击桌面启动App，阅读并同意相关协议","取消","退出",this::finish,this::finish);
            return;
        }
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                if (uri.toString().startsWith("content://")) {
                    String filePath = StorageHelper.getFilePathFromContentUri(this,uri);
                    openPath(filePath);
                }else {
                    showAddTorrentPopup(uri.toString().trim());
                }
            }
        }
    }

    private void openPath(String filePath ){
        TTTorrentInfo torrentInfo = TTDownloadService.getInstance().openTorrent(filePath,"",false);
        Intent intent1 = new Intent(this, TorrentDetailActivity.class);
        intent1.putExtra(TTTorrentInfo.INTENT_TTTORRENT_INFO,torrentInfo);
        startActivity(intent1);
    }

    @Override
    protected void onResume() {
        super.onResume();

       Disposable disposable = Observable.interval(500,TimeUnit.MILLISECONDS)
                .take(1)
                .map(aLong -> {
                    return ClipboardHelper.getClipboardContent(this,false);
                })
                .subscribe(str -> {
                    if (!isRequest) {return;}
                    if (!TextUtils.isEmpty(str)) {
                        if (ClipboardHelper.checkTextIsTorrent(str)) {
                            showTorrentAlert(str);
                        }
                    }
                }, Throwable::printStackTrace);
       addDisposable(disposable);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    private void initViewPager() {
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(pagerAdapter);
    }

    private void initTabBar() {
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        TabBarAdapter tabBarAdapter = new TabBarAdapter(tabBarItemModelList,viewPager);
        commonNavigator.setAdapter(tabBarAdapter);
        magicIndicator.setNavigator(commonNavigator);

        ViewPagerHelper.bind(magicIndicator,viewPager);
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

                        isRequest = true;


                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        ShowUserContentPopupView showUserContentPopupView = new ShowUserContentPopupView(
                                MainActivity.this,
                                "权限请求",
                                getResources().getString(R.string.permissions_message),
                                "请求权限",
                                "取消",
                                index -> {
                                    if (index == 1) {
                                        if (doNotAskAgain) {
                                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
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

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                showToast("再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            }else {
                if(!isFinishing()){
                    MobclickAgent.onKillProcess(getApplicationContext());
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTDownloadService.getInstance().destroy(this);
        Process.killProcess(Process.myPid());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OPEN_TORRENT && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();

                File file = UriUtils.uri2File(uri);
                String path = file.getPath();
                openPath(path);
            }
        }
    }
}