package com.chuanyun.downloader.popup;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.chuanyun.downloader.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class TTBasePopupView extends BasePopupView {

    private LoadingPopupView loadingPopupView;

    private Unbinder unbinder;

    public TTBasePopupView(@NonNull Context context) {
        super(context);
    }

    protected abstract int getContentLayout();

    @Override
    protected int getInnerLayoutId() {
        return getContentLayout();
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
}
