package com.chuanyun.downloader.tabbar.me.ui;

import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.coorchice.library.SuperTextView;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.httpService.HttpConfig;
import com.chuanyun.downloader.login.engine.UserEngine;
import com.chuanyun.downloader.login.model.LoginModel;
import com.chuanyun.downloader.login.model.UserInfoModel;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.popup.ShowUserContentPopupView;
import com.chuanyun.downloader.tabbar.me.VipGoodsAdapter;
import com.chuanyun.downloader.tabbar.me.model.VipGoodsData;
import com.chuanyun.downloader.tabbar.me.model.VipGoodsModel;
import com.chuanyun.downloader.tabbar.me.model.VipPayModel;
import com.chuanyun.downloader.utils.ClipboardHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VipCenterActivity extends BaseActivity {

    @BindView(R.id.vip_rv)
    RecyclerView recyclerView;

    @BindView(R.id.buy_tv)
    SuperTextView superTextView;

    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    @BindView(R.id.radio_ali)
    RadioButton aliButton;

    @BindView(R.id.radio_wx)
    RadioButton wxButton;

    @BindView(R.id.vip_cb)
    CheckBox vipCB;

    @BindView(R.id.user_vip_xz)
    TextView userVIPXZ;

    @BindView(R.id.web_ll)
    LinearLayout webLL;

    private VipGoodsAdapter goodsAdapter;

    private UserEngine userEngine;

    private int currentSelectIndex = 0;

    private int payType = 0;

    private ApiIndexModel indexModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vip_center;
    }

    @Override
    protected void initData() {
        super.initData();

        userEngine = new UserEngine(this);

        indexModel = App.getApp().getApiIndexModel();

        getGoodsData();


//        AppUtils.isAppInstalled("com.eg.android.AlipayGphone")
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        checkShowNotice();

        goodsAdapter = new VipGoodsAdapter(null);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(goodsAdapter);

        goodsAdapter.setOnItemClickListener((adapter, view, position) -> {
            VipGoodsModel goodsModel = goodsAdapter.getItem(position);
            if (goodsModel.getItemType() != VipGoodsModel.MODEL_TYPE_GOODS) {
                return;
            }
            if (currentSelectIndex == position) {
                return;
            }

            VipGoodsModel oldGoodsModel = goodsAdapter.getItem(currentSelectIndex);
            oldGoodsModel.setSelect(false);

            goodsModel.setSelect(true);

            adapter.notifyItemChanged(currentSelectIndex);
            adapter.notifyItemChanged(position);

            currentSelectIndex = position;

            configBuyTextView();
        });

        goodsAdapter.addChildClickViewIds(R.id.copy_tv);
        goodsAdapter.setOnItemChildClickListener((adapter,view,position) -> {
            VipGoodsModel vipGoodsModel = goodsAdapter.getItem(position);
            if (vipGoodsModel.getItemType() == VipGoodsModel.MODEL_TYPE_INV) {
                if (view.getId() == R.id.copy_tv) {
                    ClipboardHelper.copyTextToClipboard(this,vipGoodsModel.getSubTitle());
                    showToast("邀请码已复制");
                }
            }
        });



        int aliButtonStatus = indexModel.getAlipay() == 1 ? View.VISIBLE : View.GONE;
        int wxButtonStatus = indexModel.getWxpay() == 1 ? View.VISIBLE : View.GONE;

        aliButton.setVisibility(aliButtonStatus);
        wxButton.setVisibility(wxButtonStatus);

        payType = indexModel.getAlipay() == 1 ? 0 : 1;


        radioGroup.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.radio_ali) {
                payType = 0;
            } else {
                payType = 1;
            }
        });

        userVIPXZ.setText("《" + indexModel.getXzbt() + "》");

        // 勾选"我已阅读并同意"时自动弹出会员须知
        vipCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && buttonView.isPressed()) {
                // 只有用户手动点击时才弹出须知，程序设置不触发
                vipCB.setChecked(false);
                userXZAction();
            }
        });

    }

    private void checkShowNotice() {
        if (indexModel.getSftcydsm() == 1) {
            Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)
                    .take(1)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        showNoticeView();
                    }, Throwable::printStackTrace);
            addDisposable(disposable);
        }
    }

    private void showNoticeView() {
        showAlertView(indexModel.getYdbt(), Html.fromHtml(indexModel.getYdnr()), "取消", indexModel.getQueding(), () -> {
        }, () -> {
        });
    }

    private void configBuyTextView() {
        VipGoodsModel vipGoodsModel = goodsAdapter.getItem(currentSelectIndex);
        superTextView.setText("立即购买(" + vipGoodsModel.getMoney() + "元)");
    }

    private String payListData = "";

    private void getGoodsData() {
        showDiaLog("", false);
        Disposable disposable = userEngine.getGoodsList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(rootModel -> {
                    if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                        payListData = rootModel.getData();
                        return createData(rootModel.getData());
                    } else {
                        return Observable.error(new Throwable(rootModel.getMsg()));
                    }
                })
                .subscribe(list -> {
                    hideLoadingDialog();
                    goodsAdapter.setNewInstance(list);
                    configBuyTextView();
                }, throwable -> {
                    hideLoadingDialog();
                    showToast(throwable.getMessage());
                });
        addDisposable(disposable);
    }

    private Observable<List<VipGoodsModel>> createData(String dataStr) {
        return Observable.just(dataStr)
                .map(str -> {
                    List<VipGoodsModel> vipGoodsModelList = new ArrayList<>();

                    LoginModel loginModel = UserLoginManager.getLoginInfo();
                    VipGoodsModel headerModel = new VipGoodsModel();
                    headerModel.setItemType(VipGoodsModel.MODEL_TYPE_HEADER);
                    headerModel.setName(loginModel.getInfo().getName());
                    headerModel.setVipTime(loginModel.getInfo().getUserVipStr());
                    int color = 0;
                    if (loginModel.getInfo().getVipStatus() == 0) {
                        color = getColor(R.color.black);
                    } else if (loginModel.getInfo().getVipStatus() == 1) {
                        color = getColor(R.color.yellow_vip_btn);
                    } else {
                        color = getColor(R.color.red);
                    }
                    headerModel.setTextColor(color);
                    vipGoodsModelList.add(headerModel);

                    VipGoodsModel vipGoodsTitleModel = new VipGoodsModel();
                    vipGoodsTitleModel.setItemType(VipGoodsModel.MODEL_TYPE_TITLE);
                    vipGoodsTitleModel.setName("会员套餐");
                    vipGoodsModelList.add(vipGoodsTitleModel);

                    VipGoodsData goodsData = JSON.parseObject(dataStr, VipGoodsData.class);
                    goodsData.getList().get(0).setSelect(true);
                    currentSelectIndex = 2;

                    for (VipGoodsModel data : goodsData.getList()) {
                        data.setItemType(VipGoodsModel.MODEL_TYPE_GOODS);
                        String[] fruits = data.getBlurb().split("-");
                        data.setSubTitle(fruits[0]);
                        data.setYuanJia(fruits[1]);
                    }
                    vipGoodsModelList.addAll(goodsData.getList());

                    VipGoodsModel vipItemTitleModel = new VipGoodsModel();
                    vipItemTitleModel.setItemType(VipGoodsModel.MODEL_TYPE_TITLE);
                    vipItemTitleModel.setName("会员权益");
                    vipGoodsModelList.add(vipItemTitleModel);

                    String[] items = new String[]{
                            "多文件下载",
                            "无限次数下载",
                            "无广告",
                            "专属客服"};
                    int[] images = new int[]{
                            R.mipmap.vip_dwjxz,
                            R.mipmap.vip_wxcsxz,
                            R.mipmap.vip_kp_ad,
                            R.mipmap.vip_zskf};

                    for (int i = 0; i < items.length; i++) {
                        String title = items[i];
                        int res = images[i];
                        VipGoodsModel itemModel = new VipGoodsModel();
                        itemModel.setImageRes(res);
                        itemModel.setName(title);
                        itemModel.setItemType(VipGoodsModel.MODEL_TYPE_ITEM);
                        vipGoodsModelList.add(itemModel);
                    }

                    return vipGoodsModelList;
                });
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }

    @OnClick(R.id.buy_tv)
    public void buyAction() {

        if (vipCB.isChecked()) {
            if (payType == 0) {
                if (AppUtils.isAppInstalled("com.eg.android.AlipayGphone")) {
                    requestPay(payType);
                } else {
                    showToast("请安装支付宝");
                }
            } else {
                if (AppUtils.isAppInstalled("com.tencent.mm")) {
                    requestPay(payType);
                } else {
                    showToast("请安装微信");
                }
            }
        } else {
            ApiIndexModel indexModel = App.getApp().getApiIndexModel();
            showToast("请先阅读并同意《" + indexModel.getXzbt() + "》");
        }
    }

    private boolean isPay = false;

    private void requestPay(int type) {
        showDiaLog("", false);
        VipGoodsModel vipGoodsModel = goodsAdapter.getItem(currentSelectIndex);
        Disposable disposable = userEngine.pay(vipGoodsModel.getId(), type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rootModel -> {
                    hideLoadingDialog();
                    if (rootModel.getCode() == HttpConfig.STATUS_OK) {
                        VipPayModel payModel = JSON.parseObject(rootModel.getData(), VipPayModel.class);
                        ShowPayActivity.start(this, payModel);
                        isPay = true;
                    } else {
                        showToast(rootModel.getMsg());
                    }

                }, throwable -> {
                    hideLoadingDialog();
                    showToast(throwable.getMessage());
                });
        addDisposable(disposable);
    }

    @OnClick(R.id.user_vip_xz)
    public void userXZAction() {
        ApiIndexModel indexModel = App.getApp().getApiIndexModel();
        ShowUserContentPopupView showUserContentPopupView = new ShowUserContentPopupView(
                this,
                indexModel.getXzbt(),
                indexModel.getXznr(),
                indexModel.getTongyi(),
                "不同意",
                index -> {
                    if (index == 1) {
                        vipCB.setChecked(true);
                    }
                });
        showCustomPopupView(showUserContentPopupView, true);
    }


    private void checkPaySuccess() {
        showDiaLog("", false);
        Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)
                .take(1)
                .flatMap(aLong -> userEngine.getUserInfo().retry(3))
                .subscribeOn(Schedulers.io())
                .flatMap(rootModel -> {
                    if (rootModel.getCode() == 200) {
                        LoginModel loginModel = UserLoginManager.getLoginInfo();
                        UserInfoModel infoModel = JSON.parseObject(rootModel.getData(), UserInfoModel.class);
                        loginModel.setInfo(infoModel);
                        UserLoginManager.setLoginInfo(loginModel);
                        return createData(payListData);
                    } else {
                        return Observable.error(new Throwable(rootModel.getMsg()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    hideLoadingDialog();
                    goodsAdapter.setNewInstance(list);
                    configBuyTextView();
                }, throwable -> {
                    hideLoadingDialog();
                });
        addDisposable(disposable);
    }

    @Override
    protected void onResume() {
        if (isPay) {
            showAlertView("提示信息", "是否已经完成支付?", "未支付", "已支付", () -> {
                isPay = false;
            }, () -> {
                checkPaySuccess();
            });
        }
        super.onResume();
    }
}
