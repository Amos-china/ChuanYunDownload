package com.chuanyun.downloader.web;

import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.activity.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class WebViewController extends BaseActivity {

    public static final String INTENT_URL = "INTENT_URL";
    public static final String INTENT_TITLE = "INTENT_TITLE";

    @BindView(R.id.title_tv)
    TextView titleTextView;

    @BindView(R.id.web_ll)
    LinearLayout webLL;

    private String url;
    private String title;

    private AgentWeb agentWeb;

    public static void loadWeb(Context context,String url, String title) {
        Intent intent = new Intent(context,WebViewController.class);
        intent.putExtra(INTENT_URL,url);
        intent.putExtra(INTENT_TITLE,title);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
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
    protected void initData() {
        super.initData();

        Intent intent = getIntent();
        title = intent.getStringExtra(INTENT_TITLE);
        url = intent.getStringExtra(INTENT_URL);
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(webLL,new LinearLayout.LayoutParams(-1,-1))
                .useDefaultIndicator()
                .createAgentWeb()
                .go(url);

        titleTextView.setText(title);
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }

}
