package com.chuanyun.downloader.popup;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.impl.LoadingPopupView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TTBaseCenterPopupview extends CenterPopupView {

    private LoadingPopupView loadingPopupView;

    private Unbinder unbinder;

    public TTBaseCenterPopupview(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        unbinder =  ButterKnife.bind(this, getPopupContentView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    public void showToast(String msg) {
        Toast toast = Toast.makeText(getContext(),msg,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public void showLoading(String text) {
        if (loadingPopupView == null) {
            loadingPopupView = (LoadingPopupView) new XPopup.Builder(getContext())
                    .dismissOnBackPressed(false)
                    .isLightNavigationBar(true).asLoading();
        }
        loadingPopupView.setTitle(text);
        loadingPopupView.setStyle(LoadingPopupView.Style.Spinner);
        loadingPopupView.show();
    }

    public void dismissLoading() {
        loadingPopupView.dismiss();
    }
}
