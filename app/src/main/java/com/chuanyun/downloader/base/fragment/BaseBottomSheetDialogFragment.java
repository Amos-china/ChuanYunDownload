package com.chuanyun.downloader.base.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.dailog.LoadDialog;


import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private BottomSheetDialog dialog;
    protected View rootView;
    private BottomSheetBehavior<View> mBehavior;
    protected Activity mActivity;
    private Unbinder unbinder;
    private LoadDialog loadingView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mActivity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();

        WindowManager.LayoutParams windowParams = window.getAttributes();

        windowParams.dimAmount = 0.5f;

        window.setAttributes(windowParams);
        window.setWindowAnimations(R.style.share_anim);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = new BottomSheetDialog(getContext(), getTheme());
        if (rootView == null) {
            //缓存下来的 View 当为空时才需要初始化 并缓存
            rootView = LayoutInflater.from(mActivity).inflate(getLayoutId(), null);
            unbinder = ButterKnife.bind(this, rootView);
            loadingView = new LoadDialog(getActivity());
        }
        dialog.setContentView(rootView);

        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        ((View) rootView.getParent()).setBackgroundColor(Color.TRANSPARENT);
        rootView.post(() -> {
            /**
             * PeekHeight 默认高度 256dp 会在该高度上悬浮
             * 设置等于 view 的高 就不会卡住
             */
            mBehavior.setPeekHeight(rootView.getHeight());
        });
        initViews();
        return dialog;
    }

    protected abstract int getLayoutId();

    public abstract void initViews();

    public void showDiaLog(String msg) {
        loadingView.showLoadingDialog(msg);
    }

    public void hideLoadingDialog() {
        if (loadingView.isShowing()) {loadingView.dismissLoadingDialog();}
    }

    public void showToast(String msg) {
        Toast toast = Toast.makeText(getContext(),msg,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //解除缓存 View 和当前 ViewGroup 的关联
        unbinder.unbind();
        ((ViewGroup) (rootView.getParent())).removeView(rootView);
        Runtime.getRuntime().gc();
    }
}
