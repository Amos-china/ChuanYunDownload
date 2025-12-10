// LazyLoadFragment.java
package com.chuanyun.downloader.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import com.blankj.utilcode.util.ThreadUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.chuanyun.downloader.base.dailog.LoadDialog;

import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class LazyLoadFragment extends Fragment {
    public String TAG = getClass().getSimpleName();
    // 是否首次可见
    private boolean mIsFirstVisible = true;
    // 当前可见状态
    private boolean currentVisibleState = false;
    // View是否已创建
    private boolean isViewCreated = false;

    protected View mRootView;
    protected Context mContext;
    protected LoadDialog mLoadingDialog;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // 生命周期观察器，处理ViewPager2的生命周期
    private final LifecycleEventObserver lifecycleObserver = (source, event) -> {
        if (event == Lifecycle.Event.ON_RESUME) {
            if (shouldTriggerVisible()) {
                dispatchVisibility(true);
            }
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            if (currentVisibleState) {
                dispatchVisibility(false);
            }
        }
        // 不再在观察者内部处理移除
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        getLifecycle().addObserver(lifecycleObserver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutID(), container, false);
        ButterKnife.bind(this, mRootView);
        mLoadingDialog = new LoadDialog(requireContext());
        mLoadingDialog.setCancelable(false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        init();

        // 恢复状态
        if (savedInstanceState != null) {
            mIsFirstVisible = savedInstanceState.getBoolean("mIsFirstVisible", true);
        }

        // 如果Fragment初始可见
        if (shouldTriggerVisible()) {
            dispatchVisibility(true);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mIsFirstVisible", mIsFirstVisible);
    }

    // 判断是否应该触发可见事件
    private boolean shouldTriggerVisible() {
        return isResumed() && getUserVisibleHint() && !isHidden();
    }

    // 分发可见性变化
    private void dispatchVisibility(boolean visible) {
        // 防止重复分发
        if (currentVisibleState == visible || !isViewCreated) {
            return;
        }

        if (visible && !shouldTriggerVisible()) {
            return;
        }

        // 检查父Fragment是否可见
        if (visible && isParentInvisible()) {
            return;
        }

        if (visible) {
            if (mIsFirstVisible) {
                mIsFirstVisible = false;
                onFirstUserVisible();
            } else {
                onUserVisible();
            }
            notifyChildFragments(true);
        } else {
            notifyChildFragments(false);
            onUserInvisible();
        }

        currentVisibleState = visible;
        Log.d(TAG, "Visibility changed: " + visible);
    }

    // 检查父Fragment是否可见
    private boolean isParentInvisible() {
        Fragment parent = getParentFragment();
        if (parent instanceof LazyLoadFragment) {
            return !((LazyLoadFragment) parent).currentVisibleState;
        }
        return parent != null && !parent.isVisible();
    }

    // 通知子Fragment
    private void notifyChildFragments(boolean visible) {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment child : fragments) {
            if (child instanceof LazyLoadFragment &&
                    !child.isHidden() && child.getUserVisibleHint()) {
                ((LazyLoadFragment) child).dispatchVisibility(visible);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        dispatchVisibility(!hidden);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        currentVisibleState = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearDisposable();
        getLifecycle().removeObserver(lifecycleObserver); // 在这里移除观察者
    }

    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    private void clearDisposable() {
        compositeDisposable.clear();
    }

    protected abstract int getLayoutID();
    protected abstract void init();

    // 首次可见时调用
    public void onFirstUserVisible() {
        Log.i(TAG, "onFirstUserVisible: ");
    }

    // 可见时调用（非首次）
    public void onUserVisible() {
        Log.i(TAG, "onUserVisible: ");
    }

    // 不可见时调用
    public void onUserInvisible() {
        Log.i(TAG, "onUserInvisible: ");
        dismissLoadingDialog();
    }

    public void showLoadingDialog(String str) {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.showLoadingDialog(str);
        }
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public void showToast(String msg) {
        ThreadUtils.runOnUiThread(() -> {
            Toast toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
    }

    public void showBottomSheet(String msg, String[] items, OnSelectListener listener) {
        new XPopup.Builder(requireContext())
                .customHostLifecycle(getLifecycle())
                .borderRadius(XPopupUtils.dp2px(requireContext(), 15))
                .dismissOnTouchOutside(true)
                .dismissOnBackPressed(true)
                .isDestroyOnDismiss(true)
                .asBottomList(msg, items, listener)
                .show();
    }

    public void showCustomPopupView(BasePopupView popupView, boolean isCanDismiss) {
        new XPopup.Builder(requireContext())
                .customHostLifecycle(getLifecycle())
                .borderRadius(XPopupUtils.dp2px(requireContext(), 15))
                .dismissOnTouchOutside(isCanDismiss)
                .dismissOnBackPressed(isCanDismiss)
                .isDestroyOnDismiss(true)
                .asCustom(popupView)
                .show();
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