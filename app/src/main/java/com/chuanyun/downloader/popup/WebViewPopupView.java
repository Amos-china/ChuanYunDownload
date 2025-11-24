package com.chuanyun.downloader.popup;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.chuanyun.downloader.R;

import butterknife.BindView;
import butterknife.OnClick;

public class WebViewPopupView extends TTBasePopupView {

    @BindView(R.id.web_ll)
    WebView webView;

    @BindView(R.id.progress_b)
    ProgressBar progressBar;

    private String loadUrl;

    public void setLoadUrl(String loadUrl) {
        this.loadUrl = loadUrl;
    }

    public WebViewPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.popup_web_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        setStateBarHeight();

        webView.loadUrl("https://www.baidu.com");

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString().trim());
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(GONE);
                }else {
                    progressBar.setVisibility(VISIBLE);
                    progressBar.setProgress(newProgress);
                }

            }
        });
    }

    @OnClick(R.id.close_im)
    public void closeAction() {
        dismiss();
    }



}
