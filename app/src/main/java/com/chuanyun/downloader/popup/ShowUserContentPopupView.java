package com.chuanyun.downloader.popup;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.listener.ViewActionListener;

import butterknife.BindView;
import butterknife.OnClick;

public class ShowUserContentPopupView extends TTBaseBottomPopupView {

    @BindView(R.id.title_tv)
    TextView titleTv;

    @BindView(R.id.content_tv)
    TextView contentTv;

    @BindView(R.id.done_stv)
    SuperTextView doneStv;

    @BindView(R.id.cancel_stv)
    SuperTextView cancelStv;

    private String title;
    private String message;
    private String doneTitle;
    private String cancelTitle;

    private ViewActionListener viewActionListener;

    public ShowUserContentPopupView(Context context,
                                    String title,
                                    String message,
                                    ViewActionListener listener) {
        super(context);

        this.title = title;
        this.message = message;
        this.viewActionListener = listener;
    }

    public ShowUserContentPopupView(Context context,
                                    String title,
                                    String message,
                                    String doneTitle,
                                    String cancelTitle,
                                    ViewActionListener listener) {
        super(context);

        this.title = title;
        this.message = message;
        this.cancelTitle = cancelTitle;
        this.doneTitle = doneTitle;
        this.viewActionListener = listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_show_user_content_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        titleTv.setText(TextUtils.isEmpty(title) ? "提示信息" : title);

        contentTv.setText(message);

        if (!TextUtils.isEmpty(doneTitle)) {
            doneStv.setText(doneTitle);
        }

        if (!TextUtils.isEmpty(cancelTitle)) {
            cancelStv.setText(cancelTitle);
        }
    }

    @OnClick(R.id.cancel_stv)
    public void cancelStvAction() {
        viewActionListener.onActionIndex(0);
        dismiss();
    }

    @OnClick(R.id.done_stv)
    public void doneStvAction() {
        viewActionListener.onActionIndex(1);
        dismiss();
    }
}
