package com.chuanyun.downloader.base.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.fragment.app.DialogFragment;


import com.blankj.utilcode.util.ScreenUtils;
import com.chuanyun.downloader.base.dailog.LoadDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseDialogFragment extends DialogFragment {

    private Unbinder unbinder;
    protected View rootView;
    private LoadDialog loadingView;
    protected Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Window window = getDialog().getWindow();

        if (rootView == null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            rootView = inflater.inflate(getLayoutId(), container, false);
            loadingView = new LoadDialog(getActivity());
            unbinder = ButterKnife.bind(this, rootView);
//            window.setLayout((int) (RxDeviceTool.getScreenWidth(getActivity()) * getWidth()), getHeight());//这2行,和上面的一样,注意顺序就行;
            window.setWindowAnimations(getAnimationId());
        }
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        init();

        return rootView;

    }

    protected abstract int getLayoutId();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext =  context;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处

            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (ScreenUtils.getScreenWidth() * getWidth());
            layoutParams.height = getHeight();
            window.setAttributes(layoutParams);
            window.setGravity(getGravity());
        }

    }

    protected abstract float getWidth();

    public abstract int getAnimationId();

    public abstract int getHeight();

    public abstract void init();

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
        unbinder.unbind();
        Runtime.getRuntime().gc();
    }

    public int getGravity() {
        return Gravity.CENTER;
    }

    public void setTouchBackActionDismiss() {
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
    }

    public void setClickBackDismiss() {
        getDialog().setCancelable(true);
    }

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

}
