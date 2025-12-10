package com.chuanyun.downloader.tabbar.me.ui;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.fragment.BaseLazyFragment;
import com.chuanyun.downloader.eventBusModel.ChangeUserInfoEvent;
import com.chuanyun.downloader.eventBusModel.LoginOutEvent;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserInfoModel;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.login.popup.UserLoginPopupView;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.popup.UserSignInPopupView;
import com.chuanyun.downloader.utils.StringEncryptor;
import com.chuanyun.downloader.web.WebViewController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MeIndexFragment extends BaseLazyFragment {

    private ApiIndexModel apiIndexModel;

    @BindView(R.id.user_name_tv)
    TextView userNameTv;

    @BindView(R.id.icon_im)
    ImageView iconIm;

    @BindView(R.id.vip_time_tv)
    TextView vipTimeTv;

    @BindView(R.id.sign_in_tv)
    TextView signInTv;

    private UserEngine userEngine = new UserEngine(getContext());

    @Override
    protected int setContentView() {
        return R.layout.fragment_me_index;
    }

    @Override
    protected void initViews() {
        setStateBarHeight();

        configView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserLoginManager.checkUserLogin()) {
            LoginModel loginModel = UserLoginManager.getLoginInfo();
            userEngine.setToken(loginModel.getToken());
            configView();
            Disposable disposable = userEngine.getUserInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(rootModel -> {
                        if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                            UserInfoModel userInfo = JSON.parseObject(rootModel.getData(),UserInfoModel.class);
                            loginModel.setInfo(userInfo);
                            UserLoginManager.setLoginInfo(loginModel);
                            configView();
                        }else {
                            Log.i(TAG, "onResume: " + rootModel.getMsg());
                        }
                    },Throwable::printStackTrace);
            addDisposable(disposable);
        }
    }

    @Override
    protected void lazyLoad() {
        apiIndexModel = App.getApp().getApiIndexModel();

        registerEventBus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void configView() {
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        if (loginModel != null) {
            userNameTv.setText(loginModel.getInfo().getName());
            vipTimeTv.setText(loginModel.getInfo().getUserVipStr());
            if (loginModel.getInfo().getVipStatus() == 0) {
                vipTimeTv.setTextColor(getContext().getColor(R.color.black));
                signInTv.setVisibility(View.VISIBLE);
            }else if (loginModel.getInfo().getVipStatus() == 1) {
                vipTimeTv.setTextColor(getContext().getColor(R.color.yellow_vip_btn));
                signInTv.setVisibility(View.GONE);
            }else {
                signInTv.setVisibility(View.VISIBLE);
                vipTimeTv.setTextColor(getContext().getColor(R.color.red));
            }
        }else {
            userNameTv.setText("立即登录");
            vipTimeTv.setText("普通用户");
            vipTimeTv.setTextColor(getContext().getColor(R.color.black));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeUserInfoEvent(ChangeUserInfoEvent changeUserInfoEvent) {
        configView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginOutEvent(LoginOutEvent loginOutEvent) {
        configView();
    }

    @OnClick(R.id.sign_in_tv)
    public void userSignInAction() {
        //昵称
        LoginModel userLoginModel = UserLoginManager.getLoginInfo();
        if (userLoginModel != null && userLoginModel.getInfo() != null) {
            userSignIn();
        }else {
            showUserLoginSheet();
        }
    }

    private void userSignIn() {
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        UserSignInPopupView popupView = new UserSignInPopupView(getContext(),loginModel.getInfo().getFen(),()->{
            showDiaLog("");
            Disposable disposable = userEngine.userSignIn()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(rootModel -> {
                        if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                            return userEngine.getUserInfo();
                        }else {
                            return Observable.error(new Throwable(rootModel.getMsg()));
                        }
                    })
                    .subscribe(rootModel -> {
                        showToast("签到成功");
                        if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                            UserInfoModel userInfo = JSON.parseObject(rootModel.getData(),UserInfoModel.class);
                            loginModel.setInfo(userInfo);
                            UserLoginManager.setLoginInfo(loginModel);
                        }
                    },throwable -> {
                        hideLoadingDialog();
                        showToast(throwable.getMessage());
                    });
            addDisposable(disposable);
        });
        showCustomPopupView(popupView,true);
    }

    @OnClick(R.id.cll_setting)
    public void settingAction() {
        Intent intent = new Intent(getContext(),SettingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.vip_cll)
    public void vipCenterAction() {
        if (UserLoginManager.checkUserLogin()) {
            Intent intent = new Intent(getContext(),VipCenterActivity.class);
            startActivity(intent);
        }else {
            showAlertView("提示信息","请登录",this::userInfoAction);
        }
    }

    @OnClick(R.id.user_rl)
    public void userInfoAction() {
        LoginModel userLoginModel = UserLoginManager.getLoginInfo();
        if (userLoginModel != null && userLoginModel.getInfo() != null) {
            Intent intent = new Intent(getContext(),UserCenterActivity.class);
            startActivity(intent);
        }else {
            showUserLoginSheet();
        }
    }

    private void showUserLoginSheet() {
        String[] items = new String[] {"立即注册","已有账户"};
        showBottomSheet("请选择",items,(index, text) -> {
            UserLoginPopupView userLoginPopupView = new UserLoginPopupView(getContext());
            userLoginPopupView.setLoginType(index);
            userLoginPopupView.setLoginListener(new UserLoginPopupView.LoginListener() {
                @Override
                public void loginSuccess(LoginModel loginModel) {
                    userEngine.setToken(loginModel.getToken());
                    configView();
                }

                @Override
                public void canRegisterCallBack() {
                    ApiIndexModel indexModel = getApiIndexModelSafe();
                    if (indexModel == null) {
                        showToast("数据初始化中，请稍后再试");
                        return;
                    }
                    showBottomSheet("可注册邮箱列表",indexModel.getCanRegisterMailList(),(i,s) -> {

                    });
                }
            });

            showCustomPopupView(userLoginPopupView,true);
        });
    }


    @OnClick(R.id.gf_kf_cll)
    public void openQQ() {
//        Intent intent = new Intent();
//        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + apiIndexModel.getQQqun()));
//        try {
//            startActivity(intent);
//        } catch (Exception e) {
//            showToast("请先安装QQ");
//        }
//
//        showAlertView(apiIndexModel.getKfbt(),
//                apiIndexModel.getKfsm(),
//                "取消",
//                apiIndexModel.getFuzhi(),() -> {
//
//                },()->{
//                    ClipboardHelper.copyTextToClipboard(getContext(),apiIndexModel.getKfqq());
//                    showToast("客服QQ已复制");
//                });
        LoginModel loginModel = UserLoginManager.getLoginInfo();
        if (loginModel == null || loginModel.getInfo() == null) {
            showToast("请先登录");
            return;
        }
        ApiIndexModel indexModel = getApiIndexModelSafe();
        if (indexModel == null) {
            showToast("数据初始化中，请稍后再试");
            return;
        }
        String userId = loginModel.getInfo().getUid();
        String enUserID = StringEncryptor.encrypt(userId);
        startWebController(indexModel.getGfkf() + enUserID,"问题反馈");

    }

    private void startWebController(String url, String title) {
        WebViewController.loadWeb(getContext(),url,title);
    }

    private ApiIndexModel getApiIndexModelSafe() {
        if (apiIndexModel == null) {
            apiIndexModel = App.getApp().getApiIndexModel();
        }
        return apiIndexModel;
    }


    @OnClick(R.id.fx_cll)
    public void shareAppAction() {
        ApiIndexModel indexModel = getApiIndexModelSafe();
        if (indexModel == null) {
            showToast("数据初始化中，请稍后再试");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,indexModel.getFenxiang());
        startActivity(Intent.createChooser(intent,"分享APP到"));
    }

    @OnClick(R.id.gy_cll)
    public void aboutAppAction() {
        //About
        Intent intent = new Intent(getContext(), AboutActivity.class);
        startActivity(intent);
    }
}
