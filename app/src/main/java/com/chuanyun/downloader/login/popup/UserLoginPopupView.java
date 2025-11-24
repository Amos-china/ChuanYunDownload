package com.chuanyun.downloader.login.popup;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.alibaba.fastjson.JSON;
import com.coorchice.library.SuperTextView;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserLoginInfoMessage;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.models.ApiRootModel;
import com.chuanyun.downloader.popup.TTBaseBottomPopupView;
import com.chuanyun.downloader.tabbar.me.ui.RestPasswordActivity;
import com.chuanyun.downloader.utils.StringEncryptor;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserLoginPopupView extends TTBaseBottomPopupView {

    public interface LoginListener {
        void loginSuccess(LoginModel loginModel);
        void canRegisterCallBack();
    }

    @BindView(R.id.edit_code)
    EditText codeEt;

    @BindView(R.id.edit_email)
    EditText emailEt;

    @BindView(R.id.edit_password)
    EditText passwordEt;

    @BindView(R.id.get_code_tv)
    SuperTextView getCodeTv;

    @BindView(R.id.code_card_view)
    CardView codeCv;

    @BindView(R.id.title_tv)
    TextView titleTv;

    @BindView(R.id.check_box)
    CheckBox checkBox;

    @BindView(R.id.zc_ts_tv)
    TextView zctsTv;

    @BindView(R.id.can_reg_list_tv)
    TextView canRegTv;

    @BindView(R.id.ivt_card_v)
    CardView ivtCv;

    @BindView(R.id.ivt_et)
    EditText ivtEt;

    private LoginListener loginListener;

    private int loginType; // 0:注册登录 1:账户登录

    private UserEngine userEngine;

    public UserLoginPopupView(@NonNull Context context) {
        super(context);
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_user_reg_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        ApiIndexModel indexModel = App.getApp().getApiIndexModel();
        zctsTv.setText(indexModel.getMailinfo());
        ivtEt.setHint(indexModel.getYqsrkts());

        if (loginType == 1) {
            codeCv.setVisibility(GONE);
            titleTv.setText("邮箱登录");
            zctsTv.setVisibility(GONE);
            canRegTv.setText("忘记密码");
            ivtCv.setVisibility(GONE);
        }

        userEngine = new UserEngine(getContext());

        if (loginType == 1) {
            UserLoginInfoMessage message = UserLoginManager.getUserLoginMessage();
            if (message != null) {
                if (message.getRemember() == 1) {
                    emailEt.setText(message.getAccount());
                    passwordEt.setText(message.getPassword());
                    checkBox.setChecked(true);
                }
            }
        }
    }

    @OnClick(R.id.cancel_tv)
    public void cancelAction() {
        dismiss();
    }

    @OnClick(R.id.login_tv)
    public void loginAction() {
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String code = codeEt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showToast("请输入邮箱");
        }else {
            if (TextUtils.isEmpty(password)) {
                showToast("请输入密码");
            }else {
                if (loginType == 0) {
                    if (TextUtils.isEmpty(code)) {
                        showToast("请输入验证码");
                    }else {
                        if (checkEmail(email)) {
                            register();
                        }else {
                            showToast("邮箱不可注册,请查看可注册邮箱列表");
                        }
                    }
                }else {
                    requestLogin();
                }
            }
        }
    }

    @OnClick(R.id.get_code_tv)
    public void getCodeAction() {
        if (!canGetCode) {return;}
        String email = emailEt.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            showToast("请输入邮箱");
        }else {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (checkEmail(email)) {
                    getEmailCode();
                }else {
                    showToast("邮箱不可注册,请查看可注册邮箱列表");
                }
            }else {
                showToast("请输入正确的邮箱地址");
            }
        }
    }

    private boolean checkEmail(String email) {
        ApiIndexModel indexModel = App.getApp().getApiIndexModel();
        boolean canReg = false;
        String[] list = indexModel.getCanRegisterMailList();
        for (String s: list) {
            if (email.endsWith(s)) {
                canReg = true;
                break;
            }
        }
        return canReg;
    }

    @OnClick(R.id.can_reg_list_tv)
    public void canRegListAction() {
        if (loginType == 1) {
            Intent intent = new Intent(getContext(), RestPasswordActivity.class);
            getContext().startActivity(intent);
        }else {
            if (loginListener != null) {
                loginListener.canRegisterCallBack();
            }
        }
    }

    private boolean canGetCode = true;

    private void getEmailCode() {
        showLoading("");
        String email = emailEt.getText().toString().trim();
        Disposable disposable = userEngine.getCode(email,0)
                .subscribeOn(Schedulers.io())
                .flatMap(rootModel -> {
                    if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                        return Observable.interval(1, TimeUnit.SECONDS).take(300);
                    } else {
                        return Observable.error(new Throwable(rootModel.getMsg()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong == 0) {
                        showToast("验证码发送成功");
                    }
                    dismissLoading();
                    getCodeTv.setText(300 - aLong + "秒后可再发送");
                    canGetCode = false;
                },throwable -> {
                    dismissLoading();
                    showToast(throwable.getMessage());
                },()->{
                    canGetCode = true;
                    getCodeTv.setText("获取验证码");
                });
        addDisposable(disposable);
    }

    private void register() {
        showLoading("");
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String uuid = App.getApp().getDeviceUuid();
        String code = codeEt.getText().toString().trim();
        String invId = ivtEt.getText().toString().trim();
        if (!checkInvId(invId)) {
            showToast("邀请码不合法");
            return;
        }

        String invValue = "";
        if (!TextUtils.isEmpty(invId)) {
            invValue = StringEncryptor.decrypt(invId);
        }

        Disposable disposable = userEngine.userReg(email,code,password,uuid)
                .subscribeOn(Schedulers.io())
                .flatMap(rootModel -> {
                    if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                        return userEngine.userLogin(email,password,uuid);
                    }else {
                        return Observable.error(new Throwable(rootModel.getMsg()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loginSuccess,this::loginError);
        addDisposable(disposable);
    }

    private boolean checkInvId(String inv) {
        if (TextUtils.isEmpty(inv)) {
            return true;
        }else {
            if (inv.length() != 10) {
                return false;
            }else {
                return true;
            }
        }
    }

    private void requestLogin() {
        showLoading("");
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String uuid = App.getApp().getDeviceUuid();
        Disposable disposable = userEngine.userLogin(email,password,uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loginSuccess,this::loginError);
     addDisposable(disposable);
    }

    private void loginSuccess(ApiRootModel<String> rootModel) {
        dismissLoading();
        if (rootModel.getCode() == HttpConfig.STATUS_OK) {
            showToast("登录成功");
            LoginModel loginModel = JSON.parseObject(rootModel.getData(),LoginModel.class);
            UserLoginManager.setLoginInfo(loginModel);

            String user = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            int remember = checkBox.isChecked() ? 1 : 0;
            UserLoginManager.updateUserMessage(user,password,remember);

            if (loginListener != null) {
                loginListener.loginSuccess(loginModel);
            }
            dismiss();
        }else {
            showToast(rootModel.getMsg());
        }
    }

    private void loginError(Throwable throwable) {
        dismissLoading();
        showToast(throwable.getMessage());
    }
}
