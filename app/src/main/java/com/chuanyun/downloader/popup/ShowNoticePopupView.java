package com.chuanyun.downloader.popup;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chuanyun.downloader.R;

import butterknife.BindView;
import butterknife.OnClick;

public class ShowNoticePopupView extends TTBaseCenterPopupview {

    @BindView(R.id.title_tv)
    TextView titleTv;

    @BindView(R.id.content_tv)
    TextView contentTv;

    @BindView(R.id.done_tv)
    TextView doneTv;

    private String title;
    private String content;
    private String doneTitle;

    private NoticePopupViewListener listener;

    public interface NoticePopupViewListener {
        void doneButtonAction();
    }

    public ShowNoticePopupView(@NonNull Context context, String title,String content,String doneTitle,NoticePopupViewListener listener) {
        super(context);

        this.title = title;
        this.content = content;
        this.doneTitle = doneTitle;
        this.listener = listener;
    }

    public ShowNoticePopupView(@NonNull Context context, String title,String content,String doneTitle) {
        super(context);

        this.title = title;
        this.content = content;
        this.doneTitle = doneTitle;
    }

    public ShowNoticePopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_custom_text_view;
    }


    @Override
    protected void onCreate() {
        super.onCreate();

        titleTv.setText(title);
        // 支持HTML富文本
        contentTv.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        doneTv.setText(doneTitle);
    }

    @OnClick(R.id.done_tv)
    public void doneAction() {
        if (listener != null) {
            listener.doneButtonAction();
        }
        dismiss();
    }
}
