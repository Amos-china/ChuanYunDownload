package com.chuanyun.downloader.base.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.AppManager;
import com.chuanyun.downloader.base.dailog.LoadDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseLazyFragment extends BaseFragment {
    protected View rootView;
    private boolean isInitView = false;
    private boolean isVisible = false;
    public Activity mActivity;
    private Unbinder unbinder;

    private LoadDialog loadingView;

    private LoadingPopupView loadingPopupView;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    private void clearDisposable() {
        compositeDisposable.clear();
    }

    @Override
    protected View onFragmentCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(setContentView(), container, false);
        }
        //绑定控件
        unbinder = ButterKnife.bind(this, rootView);
        mActivity = getActivity();
        initBundle();
        initViews();
//        initImmersionBar();
        isInitView = true;
        isCanLoadData();

        loadingView = new LoadDialog(getContext());

        loadingPopupView = createLoadingPopupView();

        return rootView;
    }

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //设置共同沉浸式样式
//        ImmersionBar.with(this).navigationBarColor(R.color.white).init();
    }


    protected void initBundle() {

    }

    @Override
    public void onResume() {
        super.onResume();
        //新版本
        if (!isHidden() && isResumed()) {
            isVisible = true;
            isCanLoadData();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: 销毁了");
        clearDisposable();
        unbinder.unbind();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void isCanLoadData() {
        //所以条件是view初始化完成并且对用户可见
        if (isInitView && isVisible) {
            lazyLoad();
            //防止重复加载数据
            isInitView = false;
        }
    }

    public void showDiaLog(String msg) {
        loadingView.setCancelable(false);
        loadingView.showLoadingDialog(msg);
    }

    public void hideLoadingDialog() {
        if (!AppManager.isActivityDestory(getContext())) {
            if (loadingView.isShowing()) {
                loadingView.dismissLoadingDialog();
            }
        }
    }


    /**
     * 加载页面布局文件
     */
    protected abstract int setContentView();

    /**
     * 让布局中的view与fragment中的变量建立起映射
     */
    protected abstract void initViews();

    /**
     * 加载要显示的数据
     */
    protected abstract void lazyLoad();

    /**
     * 获取状态栏高度 直接获取属性，通过getResource
     *
     * @return
     */
    public void setStateBarHeight() {
        View viewBar = rootView.findViewById(R.id.status_bar);
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


    public void showAlertView(String title, String message, OnConfirmListener confirmListener) {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asConfirm(title,message,confirmListener)
                .show();
    }

    public void showPopupViewLoading(String string) {
        loadingPopupView.setTitle(string)
                .show();
    }

    public void popupViewLoadingDismiss() {
        loadingPopupView.dismiss();
    }

    public void delayShowPopupViewDialog(String title, int delay, Runnable runnable) {
        loadingPopupView.setTitle(title).show()
                .delayDismissWith(delay * 1000,runnable);
    }

    private LoadingPopupView createLoadingPopupView() {
        return new XPopup.Builder(getContext())
                .dismissOnBackPressed(false)
                .popupHeight(120)
                .popupWidth(120)
                .dismissOnTouchOutside(false)
                .isDestroyOnDismiss(false)
                .isLightNavigationBar(true)
                .asLoading("", LoadingPopupView.Style.Spinner);
    }

    public void showBottomSheet(String msg, String[] items, OnSelectListener listener) {
        new XPopup.Builder(getContext())
                .customHostLifecycle(getLifecycle())
                .borderRadius(XPopupUtils.dp2px(getContext(),15))
                .dismissOnTouchOutside(true)
                .dismissOnBackPressed(true)
                .isDestroyOnDismiss(true)
                .asBottomList(msg,items,listener)
                .show();
    }

    public void showInputAlter(String title, String content, String hint, OnInputConfirmListener listener) {
        new XPopup.Builder(getContext())
                .customHostLifecycle(getLifecycle())
                .borderRadius(XPopupUtils.dp2px(getContext(),8))
                .dismissOnTouchOutside(true)
                .dismissOnBackPressed(true)
                .isDestroyOnDismiss(true)
                .asInputConfirm(title,content,hint,listener)
                .show();
    }

    public void showCustomPopupView(BasePopupView popupView, boolean isCanDismiss) {
        new XPopup.Builder(getContext())
                .customHostLifecycle(getLifecycle())
                .borderRadius(XPopupUtils.dp2px(getContext(),15))
                .dismissOnTouchOutside(isCanDismiss)
                .dismissOnBackPressed(isCanDismiss)
                .isDestroyOnDismiss(true)
                .asCustom(popupView)
                .show();
    }

    public void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void showAlertView(String title,
                              CharSequence msg,
                              String cancelTitle,
                              String doneTitle,
                              OnCancelListener cancelListener,
                              OnConfirmListener confirmListener) {
        new XPopup.Builder(getContext())
                .customHostLifecycle(getLifecycle())
                .borderRadius(XPopupUtils.dp2px(getContext(),15))
                .dismissOnTouchOutside(true)
                .dismissOnBackPressed(true)
                .isDestroyOnDismiss(true)
                .asConfirm(title,msg,cancelTitle,doneTitle,confirmListener,cancelListener,false)
                .show();
    }

    public void showAlertView(String title,
                              CharSequence msg,
                              String cancelTitle,
                              String doneTitle,
                              boolean dismissTouchOutside,
                              OnCancelListener cancelListener,
                              OnConfirmListener confirmListener) {
        new XPopup.Builder(getContext())
                .customHostLifecycle(getLifecycle())
                .borderRadius(XPopupUtils.dp2px(getContext(),15))
                .dismissOnTouchOutside(dismissTouchOutside)
                .dismissOnBackPressed(dismissTouchOutside)
                .isDestroyOnDismiss(true)
                .asConfirm(title,msg,cancelTitle,doneTitle,confirmListener,cancelListener,false)
                .show();
    }

}
