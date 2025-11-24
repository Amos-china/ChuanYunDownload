package com.chuanyun.downloader.popup;

import android.content.Context;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.web.WebViewController;

import butterknife.OnClick;

public class AppPrivacyPopupView extends TTBaseCenterPopupview{

    public interface OnButtonClickListener {
        void cancel();
        void next();
    }

    private OnButtonClickListener onButtonClickListener;

    public AppPrivacyPopupView(Context context, OnButtonClickListener listener) {
        super(context);
        this.onButtonClickListener = listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_app_privacy_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @OnClick(R.id.user_yszc_tv)
    public void privacyAction() {

        startWebActivity("https://api-chuanyun.kuaiyunpan.cc/yinsi.html","隐私政策");
    }

    @OnClick(R.id.user_yhxy_tv)
    public void userAgreementAction() {
        startWebActivity("https://api-chuanyun.kuaiyunpan.cc/xieyi.html","用户协议");
    }

    @OnClick(R.id.msg_u_um_policy_tv)
    public void umengPolicyAction() {
        startWebActivity("https://www.umeng.com/page/policy","友盟隐私权政策");
    }

    private void startWebActivity(String url, String title) {
        WebViewController.loadWeb(getContext(),url,title);
    }

    @OnClick(R.id.user_cancel_tv)
    public void cancelAction() {
        dismiss();
        onButtonClickListener.cancel();
    }

    @OnClick(R.id.user_next_tv)
    public void userNextAction() {
        dismiss();
        onButtonClickListener.next();
    }
}
