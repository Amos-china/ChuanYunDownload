package com.chuanyun.downloader.popup;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chuanyun.downloader.R;

import butterknife.BindView;
import butterknife.OnClick;

public class UserSignInPopupView extends TTBaseCenterPopupview{

    @BindView(R.id.fen_tv)
    TextView fenTv;

    private int fen;
    private SigInPopupViewListener listener;

    public interface SigInPopupViewListener {
        void sigInButtonAction();
    }

    public UserSignInPopupView(@NonNull Context context,int fen, SigInPopupViewListener listener) {
        super(context);
        this.fen = fen;
        this.listener = listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_sign_in_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        fenTv.setText("当前总积分:" + fen);
    }

    @OnClick(R.id.sign_in_tv)
    public void signInAction() {
        if (listener != null) {
            listener.sigInButtonAction();
        }
        dismiss();
    }
}
