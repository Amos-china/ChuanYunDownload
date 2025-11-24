package com.chuanyun.downloader.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.lxj.xpopup.XPopup;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.dao.WebHistoryDao;
import com.chuanyun.downloader.models.RecommendURLModel;
import com.chuanyun.downloader.models.WebHistoryInfo;
import com.chuanyun.downloader.popup.InputURLPopupView;
import com.chuanyun.downloader.popup.WebHistoryListPopup;
import com.chuanyun.downloader.utils.BitmapUtil;
import com.chuanyun.downloader.utils.ClipboardHelper;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TTWebActivity extends BaseActivity {

    @BindView(R.id.web_ll)
    LinearLayout webLL;

    @BindView(R.id.title_tv)
    TextView titleTv;

    @BindView(R.id.icon_im)
    ImageView iconImageView;

    @BindView(R.id.tb_collect_im)
    ImageView collectIm;

    private AgentWeb mAgentWeb;

    private RecommendURLModel urlNavModel;

    private WebHistoryDao webHistoryDao;

    private ClipboardHelper clipboardListener;

    private WebHistoryInfo collectWebHistoryInfo;

    public static void loadUrl(Context context, RecommendURLModel urlNavModel) {
        Intent intent = new Intent(context,TTWebActivity.class);
        intent.putExtra(RecommendURLModel.URL_NAV_MODEL_INTENT,urlNavModel);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_web_view;
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        clipboardListener.stopListening();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        clipboardListener.startListening();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();


    }

    private void initClipboardHelper() {
        clipboardListener = new ClipboardHelper(this);

        clipboardListener.setClipboardChangeListener(content -> {
            if (ClipboardHelper.checkTextIsTorrent(content)) {
                showTorrentAlert(content);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(webLL,new LinearLayout.LayoutParams(-1,-1))
                .useDefaultIndicator()
                .setWebViewClient(new WebViewClient(){

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        String requestUrl = request.getUrl().toString().trim();
                        if (requestUrl.startsWith("magnet:?xt=urn:") ||
                                requestUrl.startsWith("ed2k://") ||
                                requestUrl.startsWith("thunder://")) {
                            showAddTorrentPopup(requestUrl);
                            return true;
                        }
                        return super.shouldOverrideUrlLoading(view, request);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        titleTv.setText(view.getTitle());
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                    }
                })
                .setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        titleTv.setText(title);
                    }

                    @Override
                    public void onReceivedIcon(WebView view, Bitmap icon) {
                        super.onReceivedIcon(view, icon);
                        if (icon == null) {
                            iconImageView.setImageResource(R.mipmap.url_sear_im);
                        }else {
                            iconImageView.setImageBitmap(icon);
                        }

                        WebHistoryInfo webHistoryInfo = new WebHistoryInfo();
                        webHistoryInfo.setDel(0);
                        webHistoryInfo.setCollect(0);
                        webHistoryInfo.setImageBitmap(icon);
                        webHistoryInfo.setUrl(view.getUrl());
                        webHistoryInfo.setTitle(view.getTitle());
                        long currentTime = System.currentTimeMillis();
                        webHistoryInfo.setCreateTime(currentTime);

                        addHistoryTable(webHistoryInfo);
                    }

                })
                .createAgentWeb()
                .go(urlNavModel.getUrl());

        titleTv.setText(urlNavModel.getName());
    }

    private void addHistoryTable(WebHistoryInfo history) {
        Disposable disposable = webHistoryDao.getWebHistoryInfoAtUrl(history.getUrl())
                .subscribeOn(Schedulers.io())
                .doOnSuccess(webHistoryInfo -> webHistoryInfo.setCreateTime(System.currentTimeMillis()))
                .onErrorReturnItem(history)
                .subscribe(webHistoryInfo -> {
                    if (webHistoryInfo.getImageBitmap() != null) {
                        String baseImageStr = BitmapUtil.bitmapToBase64(webHistoryInfo.getImageBitmap());
                        webHistoryInfo.setByteImage(baseImageStr);
                    }

                    int src = webHistoryInfo.getCollect() == 0 ? R.mipmap.tb_collect_n : R.mipmap.tb_collect_s;
                    collectIm.setImageResource(src);
                    collectWebHistoryInfo = webHistoryInfo;

                    webHistoryDao.insertHistoryInfo(webHistoryInfo).subscribeOn(Schedulers.io()).subscribe();
                },throwable -> {

                });
        addDisposable(disposable);
    }

    @Override
    protected void initData() {
        super.initData();

        urlNavModel = (RecommendURLModel) getIntent().getSerializableExtra(RecommendURLModel.URL_NAV_MODEL_INTENT);

        webHistoryDao = App.getApp().getAppDataBase().webHistoryDao();

        initClipboardHelper();
    }

    @OnClick(R.id.title_tv)
    public void titleTvActon() {
        searchBtnAction();
    }

    @OnClick(R.id.search_ll)
    public void searchBtnAction() {
        InputURLPopupView inputURLPopupView = new InputURLPopupView(this,text -> {
            mAgentWeb.getWebCreator().getWebView().loadUrl(text);
        });

        new XPopup.Builder(this)
                .isLightStatusBar(true)
                .autoOpenSoftInput(true)
                .dismissOnTouchOutside(false)
                .isDestroyOnDismiss(true)
                .asCustom(inputURLPopupView)
                .show();
    }

    @OnClick(R.id.close_im)
    public void closeImAction() {
        finish();
    }

    @OnClick(R.id.tb_back_im)
    public void toolBarBackAction() {
        if (mAgentWeb.getWebCreator().getWebView().canGoBack()) {
            mAgentWeb.getWebCreator().getWebView().goBack();  // 后退
        }
    }


    @OnClick(R.id.tb_go_im)
    public void toolBarGoAction() {
        if (mAgentWeb.getWebCreator().getWebView().canGoForward()) {
            mAgentWeb.getWebCreator().getWebView().goForward(); // 前进
        }
    }

    @OnClick(R.id.tb_reload_im)
    public void toolBarReloadAction() {
        mAgentWeb.getWebCreator().getWebView().reload();
    }

    @OnClick(R.id.tb_collect_im)
    public void toolBarCollectAction() {
        if (collectWebHistoryInfo == null) {
            return;
        }

        String url = mAgentWeb.getWebCreator().getWebView().getUrl();
        int collect = collectWebHistoryInfo.getCollect() == 0 ? 1 : 0;
        Disposable disposable = webHistoryDao.setWebHistoryInfoCollect(collect,url).subscribeOn(Schedulers.io()).subscribe(()->{},throwable -> {});
        addDisposable(disposable);

        int src = collect == 0 ? R.mipmap.tb_collect_n : R.mipmap.tb_collect_s;
        collectIm.setImageResource(src);
    }

    @OnClick(R.id.tb_list_im)
    public void toolBarListAction() {
        WebHistoryListPopup webHistoryListPopup = new WebHistoryListPopup(this);
        webHistoryListPopup.setHistoryItemClickListener(info -> {
            titleTv.setText(info.getUrl());
            mAgentWeb.getWebCreator().getWebView().loadUrl(info.getUrl());
        });

        new XPopup.Builder(this)
                .isDestroyOnDismiss(true)
                .isViewMode(true) //使用了Fragment，必须开启View模式
                .asCustom(webHistoryListPopup)
                .show();
    }
}
