package com.chuanyun.downloader.popup;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.utils.ClipboardHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class InputURLPopupView extends TTBasePopupView {

    public interface SearchDismissListener {
        void searchMessage(String text);
    }

    @BindView(R.id.search_et)
    EditText searchEt;

    @BindView(R.id.open_url_tv)
    TextView openUrlTv;

    @BindView(R.id.baidu_search_tv)
    TextView baiduSearchTv;

    private SearchDismissListener searchDismissListener;

    public InputURLPopupView(@NonNull Context context, SearchDismissListener listener) {
        super(context);
        searchDismissListener = listener;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.popup_input_url;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        setStateBarHeight();

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                baiduSearchTv.setText(charSequence.toString().trim());
                openUrlTv.setText(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick(R.id.baidu_search_ll)
    public void baiduSearchAction() {
        String message = searchEt.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            String searchStr = "https://cn.bing.com/search?q=" + message;
            searchDismissListener.searchMessage(searchStr);
            dismiss();
        }
    }

    @OnClick(R.id.open_url_ll)
    public void openUrlAction() {
        String message = searchEt.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            if (!message.startsWith("https://") || !message.startsWith("http://")) {
                message = "https://" + message;
            }
            searchDismissListener.searchMessage(message);
            dismiss();
        }
    }

    @OnClick(R.id.copy_tv)
    public void copyAction() {
        String value = ClipboardHelper.getClipboardContent(getContext(),true);
        searchEt.setText(value);
    }

    @OnClick(R.id.cancel_tv)
    public void cancelAction() {
        dismiss();
    }

    @OnClick(R.id.clear_im)
    public void clearAction() {
        searchEt.setText("");
    }

}
