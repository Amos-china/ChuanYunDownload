package com.chuanyun.downloader.popup;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chuanyun.downloader.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class OnlinePlayPasrePopup extends TTBaseCenterPopupview {

    @BindView(R.id.title_tv)
    TextView titleView;

    @BindView(R.id.loadView)
    ImageView loadImageView;

    private Disposable disposable;

    private int delayTimer;
    private Context context;

    public interface TimerNext {
        void next(Long l);
    }

    public OnlinePlayPasrePopup(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_loading_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        setTitleViewText("预计还剩" + delayTimer + "秒");


        Glide.with(context)
                .asGif() // 指定加载为GIF
                .load(R.drawable.online_play_src) // 加载GIF资源
                .into(loadImageView);
    }

    public void setDelayDismiss(int delay, TimerNext timerNext,  Action action) {
        delayTimer = delay;
        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .take(delay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    timerNext.next(aLong);
                    setTitleViewText("预计还剩" + (delayTimer - aLong - 1) + "秒");
                },throwable -> {},action);
    }

    public void disposeTimer() {
        disposable.dispose();
    }

    private void setTitleViewText(String text) {
        titleView.setText(text);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
