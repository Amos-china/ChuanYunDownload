package com.chuanyun.downloader.tabbar.me.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebViewClient;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.tabbar.me.model.VipPayModel;

import butterknife.BindView;
import butterknife.OnClick;

public class ShowPayActivity extends BaseActivity {

    @BindView(R.id.web_ll)
    LinearLayout webLL;

    @BindView(R.id.zf_ts_tv)
    TextView zfTsTv;

    private AgentWeb agentWeb;
    private VipPayModel vipPayModel;

    public static void start(Context context, VipPayModel vipPayModel) {
        Intent intent = new Intent(context,ShowPayActivity.class);
        intent.putExtra(VipPayModel.INTENT_KEY,vipPayModel);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_pay;
    }

    @Override
    protected void initData() {
        super.initData();

        vipPayModel = (VipPayModel) getIntent().getSerializableExtra(VipPayModel.INTENT_KEY);
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        zfTsTv.setText(App.getApp().getApiIndexModel().getZfbhqts());


        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(webLL,new LinearLayout.LayoutParams(-1,-1))
                .useDefaultIndicator()
                .setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }
                })
                .createAgentWeb()
                .go(vipPayModel.getPayUrl());
    }


    @Override
    protected void onPause() {
        agentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        agentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        agentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (agentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }
}
