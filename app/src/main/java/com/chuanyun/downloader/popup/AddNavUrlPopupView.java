package com.chuanyun.downloader.popup;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.blankj.utilcode.util.RegexUtils;
import com.chuanyun.downloader.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AddNavUrlPopupView extends TTBaseBottomPopupView {

    @BindView(R.id.nav_url_et)
    EditText navUrlEt;

    @BindView(R.id.nav_name_et)
    EditText navNameEt;

    private AddNavUrlPopupViewListener listener;

    public interface AddNavUrlPopupViewListener {
        void doneActionCallBack(String url,String name);
        void managerActionCallBack();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_add_nav_url_view;
    }

    public AddNavUrlPopupView(Context context, AddNavUrlPopupViewListener listener) {
        super(context);
        this.listener = listener;
    }

    @OnClick(R.id.manager_tv)
    public void managerAction() {
        if (listener != null) {
            listener.managerActionCallBack();
        }
    }

    @OnClick(R.id.clear_url_im)
    public void clearUrlImAction() {
        navUrlEt.setText("");
    }

    @OnClick(R.id.clear_name_im)
    public void clearNameImAction() {
        navNameEt.setText("");
    }


    @OnClick(R.id.cancel_tv)
    public void cancelAction() {
        dismiss();
    }

    @OnClick(R.id.done_tv)
    public void doneAction() {
        String url = navUrlEt.getText().toString().trim();
        String name = navNameEt.getText().toString().trim();

        if (!RegexUtils.isURL(url)) {
            showToast("输入的网址有误");
            return;
        }

        if (TextUtils.isEmpty(name)) {
            showToast("请输入名称");
            return;
        }

        if (listener != null) {
            listener.doneActionCallBack(url,name);
        }
        dismiss();
    }
}
