package com.chuanyun.downloader.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.blankj.utilcode.util.ThreadUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.AppManager;
import com.chuanyun.downloader.base.api.ApiEngine;
import com.chuanyun.downloader.base.dailog.LoadDialog;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.popup.AddTorrentPopup;
import com.chuanyun.downloader.popup.AppUpdatePopupView;
import com.chuanyun.downloader.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

public abstract class BaseActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_OPEN_TORRENT = 1;

    public String TAG = getClass().getSimpleName();

    protected Activity mActivity;

    private LoadDialog loadingView;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ApiEngine apiEngine;

    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    private void clearDisposable() {
        compositeDisposable.clear();
    }

    public void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppManager.getInstance().addActivity(this);
        mActivity = this;
        setContentView(getLayoutId());

        loadingView = new LoadDialog(this);

        //绑定控件
        ButterKnife.bind(this);
        //初始化沉浸式
        //初始化数据

        apiEngine = new ApiEngine(this);

        initData();

        //view与数据绑定
        initView();

        setStatusBarTextColor();

        //设置监听
        setListener();


    }

    public void setStatusBarTextColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏文字颜色为深色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().removeActivity(this);

        clearDisposable();

        unRegisterEventBus();

        Log.i(TAG, "onDestroy: 销毁了");

        if (loadingView != null) {
            loadingView = null;
        }
    }

    /**
     * 子类设置布局Id
     *
     * @return the layout id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //设置共同沉浸式样式
//        ImmersionBar.with(this).navigationBarColor(R.color.white).init();
    }

    protected void initData() {
    }

    protected void initView() {
    }

    protected void setListener() {
    }

    public void showToast(String msg) {
        ThreadUtils.runOnUiThread(()-> {
            Toast toast = Toast.makeText(this,msg,Toast.LENGTH_LONG);
            toast.show();
        });
    }

    public void loadImage(String url, ImageView imageView) {
        //添加判断
//        if(!isDestroy(this)){
//            RoundedCorners roundedCorners = new RoundedCorners(20);
//            RequestOptions options = new RequestOptions().bitmapTransform(roundedCorners);
//            Glide.with(this).load(url).apply(options).into(imageView);
//        }
    }

    public void showDiaLog(String msg) {
        loadingView.showLoadingDialog(msg);
    }

    public void showDiaLog(String msg,boolean canCancel) {
        loadingView.showLoadingDialog(msg);
        loadingView.setCancelable(canCancel);
    }

    public void loadingDelayAutoDismiss(int delay, Action action) {
        loadingView.setCancelable(false);
        loadingView.loadingDelayAutoDismiss(delay,action);
    }

    public void hideLoadingDialog() {
        if (!AppManager.isActivityDestory(this)) {
            if (loadingView.isShowing()) {
                loadingView.dismissLoadingDialog();
            }
        }
    }

    /**
     * 判断Activity是否Destroy
     * @param
     * @return
     */
    public static boolean isDestroy(Activity mActivity) {
        if (mActivity== null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取状态栏高度 直接获取属性，通过getResource
     *
     * @return
     */
    public void setStateBarHeight() {
        View viewBar = findViewById(R.id.status_bar);
        setStateBarHeight(viewBar, 0);
    }

    public void setStateBarHeight(View viewBar, int addHeight) {
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        if (result <= 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = viewBar.getLayoutParams();
        layoutParams.height = result + addHeight;
        viewBar.setLayoutParams(layoutParams);
        Log.d("ClassName", "setStateBarHeight: layoutParams.height " + layoutParams.height);
    }



    public void showAddTorrentPopup(String text) {
        showCustomPopupView(new AddTorrentPopup(this,text,()-> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/x-bittorrent");
            startActivityForResult(intent, REQUEST_CODE_OPEN_TORRENT);
        }),true);
    }

    public void showTorrentAlert(String text) {
        showAlertView("提示信息","剪切板上有新的链接是否解析？", ()-> {showAddTorrentPopup(text);});
    }

    public void showAlertView(String meg, String cancelText, String doneText, OnConfirmListener confirmListener, OnCancelListener cancelListener) {
        new XPopup.Builder(this)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asConfirm("提示信息",Html.fromHtml(meg, Html.FROM_HTML_MODE_LEGACY),cancelText,doneText,confirmListener,cancelListener,false)
                .show();
    }

    public void showAlertView(String title, String message, OnConfirmListener confirmListener) {
        new XPopup.Builder(this)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asConfirm(title,Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY),confirmListener)
                .show();
    }


    private AppUpdatePopupView appUpdatePopupView;

    private void loadNewApp(ApiIndexModel indexModel) {
        DownloadManager manager = new DownloadManager.Builder(this)
                .apkUrl(indexModel.getZlurl())
                .apkName("穿云下载.apk")
                .smallIcon(R.mipmap.logo)
                .showNotification(true)
                .onDownloadListener(new OnDownloadListener() {
                    @Override
                    public void start() {

                    }

                    @Override
                    public void downloading(int i, int i1) {
                        appUpdatePopupView.uploadProgress(i,i1);
                    }

                    @Override
                    public void done(@NonNull File file) {

                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void error(@NonNull Throwable throwable) {

                    }
                })
                .build();
        manager.download();
    }

    public void showAppUpdateView(ApiIndexModel indexModel) {
        appUpdatePopupView = new AppUpdatePopupView(this,indexModel,(type) -> {
            if (type == 0) {
                loadNewApp(indexModel);
            }else {
                openOtherWeb(indexModel);
            }
        });
        showCustomPopupView(appUpdatePopupView,false);
    }

    private void openOtherWeb(ApiIndexModel indexModel) {
        openUrl(indexModel.getGxurl());
    }

    public void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            showToast("没有找到可以打开此链接的应用");
        }
    }

    public boolean checkIndexModel(ApiIndexModel indexModel) {
        if (indexModel.getSfyxfx() == 1) {
            return true;
        }else {
            Disposable disposable = Observable.interval(1,TimeUnit.SECONDS)
                    .take(1)
                    .subscribe(along -> finish());
            addDisposable(disposable);
        }
        return false;
    }

    public boolean checkAppVersion(ApiIndexModel indexModel) {
        if (indexModel.getBbh() != AppUtils.getVersionCode(this)) {
            showAppUpdateView(indexModel);
            return false;
        }
        return true;
    }

    public void showBottomPopupView(BasePopupView popupView, boolean canCancel) {
        showCustomPopupView(popupView,canCancel);
    }

    public void showCustomPopupView(BasePopupView popupView,boolean canCancel) {
        new XPopup.Builder(this)
                .isDestroyOnDismiss(true)
                .borderRadius(XPopupUtils.dp2px(this,15.f))
                .dismissOnTouchOutside(canCancel)
                .dismissOnBackPressed(canCancel)
                .asCustom(popupView)
                .show();
    }

    public void showBottomSheet(String msg, String[] items, OnSelectListener listener) {
        new XPopup.Builder(this)
                .borderRadius(XPopupUtils.dp2px(this,15))
                .dismissOnTouchOutside(true)
                .dismissOnBackPressed(true)
                .isDestroyOnDismiss(true)
                .asBottomList(msg,items,listener)
                .show();
    }

    public void showBottomIconSheet(String msg,String[] items, int[] icons,int checkIndex, OnSelectListener listener) {
        new XPopup.Builder(this)
                .borderRadius(XPopupUtils.dp2px(this,15))
                .dismissOnTouchOutside(true)
                .dismissOnBackPressed(true)
                .isDestroyOnDismiss(true)
                .asBottomList(msg,items,icons,checkIndex,listener)
                .show();
    }

    public void showAlertView(String title,
                              CharSequence msg,
                              String cancelTitle,
                              String doneTitle,
                              OnCancelListener cancelListener,
                              OnConfirmListener confirmListener) {
        new XPopup.Builder(this)
                .borderRadius(XPopupUtils.dp2px(this,15))
                .dismissOnTouchOutside(true)
                .dismissOnBackPressed(true)
                .isDestroyOnDismiss(true)
                .asConfirm(title,msg,cancelTitle,doneTitle,confirmListener,cancelListener,false)
                .show();
    }
}
