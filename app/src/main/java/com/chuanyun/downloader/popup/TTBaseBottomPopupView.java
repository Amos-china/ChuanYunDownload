package com.chuanyun.downloader.popup;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.impl.LoadingPopupView;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class TTBaseBottomPopupView extends BottomPopupView {

    private LoadingPopupView loadingPopupView;

    private Unbinder unbinder;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public TTBaseBottomPopupView(@NonNull Context context) {
        super(context);
    }

    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    private void clearComposable() {
        compositeDisposable.clear();
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
        clearComposable();
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
