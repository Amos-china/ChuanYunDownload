package com.chuanyun.downloader.tabbar.home.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.lxj.xpopup.XPopup;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.api.ApiEngine;
import com.chuanyun.downloader.base.fragment.BaseLazyFragment;
import com.chuanyun.downloader.dao.UrlNavDao;
import com.chuanyun.downloader.eventBusModel.NavUrlChangeEvent;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.models.RecommendURLModel;
import com.chuanyun.downloader.popup.AddNavUrlPopupView;
import com.chuanyun.downloader.popup.WebHistoryListPopup;
import com.chuanyun.downloader.tabbar.home.adapter.HomeRvAdapter;
import com.chuanyun.downloader.tabbar.home.model.UrlNavModel;
import com.chuanyun.downloader.web.TTWebActivity;
import com.chuanyun.downloader.web.WebViewController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeIndexFragment extends BaseLazyFragment {

    @BindView(R.id.search_et)
    EditText searchEt;

    @BindView(R.id.url_nav_rv)
    RecyclerView urlNavRv;

    private HomeRvAdapter navAdapter;

    private ApiEngine apiEngine;
    private UrlNavDao urlNavDao;

    @Override
    protected int setContentView() {
        return R.layout.fragment_home_index;
    }



    @Override
    protected void initViews() {
        setStateBarHeight();

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);       // 设置排列方向为横向
        layoutManager.setFlexWrap(FlexWrap.WRAP);               // 启用自动换行
        layoutManager.setJustifyContent(JustifyContent.FLEX_START); // 对齐方式为起始对齐
        urlNavRv.setLayoutManager(layoutManager);

        navAdapter = new HomeRvAdapter(null);
        urlNavRv.setAdapter(navAdapter);

        navAdapter.setOnItemClickListener((adapter, view, position) -> {
            RecommendURLModel model = navAdapter.getItem(position);
            TTWebActivity.loadUrl(getContext(),model);
        });



        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 执行发送操作
                searchText();
                return true; // 返回 true 表示已处理该事件
            }
            return false; // 返回 false 表示没有处理该事件
        });
    }

    private void addNavUrlAt(String url,String name) {
        UrlNavModel model = new UrlNavModel();
        model.setCreateTime(System.currentTimeMillis());
        model.setUrl(url);
        model.setName(name);
        model.setTagName(name.substring(0,1));

        RecommendURLModel recommendURLModel = new RecommendURLModel();
        recommendURLModel.setName(name);
        recommendURLModel.setUrl(url);

        navAdapter.addData(navAdapter.getData().size() - 1,recommendURLModel);

        Disposable disposable = urlNavDao.insertUrlNavInfo(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()-> {

                },Throwable::printStackTrace);
        addDisposable(disposable);
    }


    @Override
    protected void lazyLoad() {
        apiEngine = new ApiEngine(getContext());
        urlNavDao = App.getApp().getAppDataBase().urlNavDao();
        Disposable disposable = getIndexData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(navAdapter::setNewInstance,Throwable::printStackTrace);
        addDisposable(disposable);

        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeNavUrlModel(NavUrlChangeEvent event) {
        navAdapter.removeAt(apiRecommendCount + event.getIndex());
    }

    private int apiRecommendCount;
    private Observable<List<RecommendURLModel>> getIndexData() {
        return Observable.zip(apiEngine.getRecommendList(),getUrlNavObservable(),(rootModel,list) -> {

            List<RecommendURLModel> tempList = rootModel.getData().getSiteList();

            apiRecommendCount = tempList.size();

            for (UrlNavModel model: list) {
                RecommendURLModel urlModel = new RecommendURLModel();
                urlModel.setUrl(model.getUrl());
                urlModel.setName(model.getName());
                tempList.add(urlModel);
            }

            return tempList;
        });
    }

    private Observable<List<UrlNavModel>> getUrlNavObservable() {
        return urlNavDao.getNavInfoList(0,200).toObservable();
    }

    @OnClick(R.id.search_tv)
    public void searchTvAction() {
        searchText();
    }

    private void searchText() {
        String searchStr = searchEt.getText().toString().trim();
        if (TextUtils.isEmpty(searchStr)) {
            showToast("请输入你想搜的关键字");
            return;
        }
        RecommendURLModel recommendURLModel = new RecommendURLModel();
        recommendURLModel.setUrl("https://cn.bing.com/search?q=" + searchStr);
        recommendURLModel.setName("必应搜索");
        TTWebActivity.loadUrl(getContext(),recommendURLModel);
    }

    @OnClick(R.id.collect_url_tv)
    public void collectUrlAction() {
        WebHistoryListPopup webHistoryListPopup = new WebHistoryListPopup(getContext());
        webHistoryListPopup.setHistoryItemClickListener(info -> {
            RecommendURLModel searchModel = new RecommendURLModel();
            searchModel.setUrl(info.getUrl());
            searchModel.setName(info.getTitle());
            TTWebActivity.loadUrl(getContext(),searchModel);
        });

        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .isViewMode(true) //使用了Fragment，必须开启View模式
                .asCustom(webHistoryListPopup)
                .show();
    }

    @OnClick(R.id.add_nav_im)
    public void addNavImAction() {
        AddNavUrlPopupView navUrlPopupView = new AddNavUrlPopupView(getContext(),
                new AddNavUrlPopupView.AddNavUrlPopupViewListener() {
                    @Override
                    public void doneActionCallBack(String url, String name) {
                        addNavUrlAt(url,name);
                    }

                    @Override
                    public void managerActionCallBack() {
                        Intent intent = new Intent(getContext(),HomeNavUrlManagerActivity.class);
                        startActivity(intent);
                    }
                });
        showCustomPopupView(navUrlPopupView,true);
    }

    @OnClick(R.id.clear_im)
    public void clearImAction() {
        searchEt.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
