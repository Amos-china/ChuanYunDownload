package com.chuanyun.downloader.tabbar.home.ui;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.activity.BaseActivity;
import com.chuanyun.downloader.dao.UrlNavDao;
import com.chuanyun.downloader.eventBusModel.NavUrlChangeEvent;
import com.chuanyun.downloader.tabbar.home.adapter.HomeIndexUrlNavAdapter;
import com.chuanyun.downloader.tabbar.home.model.UrlNavModel;
import com.chuanyun.downloader.utils.ClipboardHelper;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeNavUrlManagerActivity extends BaseActivity {

    @BindView(R.id.url_nav_rv)
    RecyclerView recyclerView;

    private HomeIndexUrlNavAdapter navAdapter;

    private UrlNavDao urlNavDao;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nav_url_manager;
    }

    @Override
    protected void initView() {
        super.initView();

        setStateBarHeight();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        navAdapter = new HomeIndexUrlNavAdapter(null);
        recyclerView.setAdapter(navAdapter);

        navAdapter.addEmptyView();

        navAdapter.setOnItemClickListener((adapter, view, position) -> {
            UrlNavModel urlNavModel = navAdapter.getItem(position);
            showBottomSheet(urlNavModel.getName(),new String[]{"复制网址","删除导航"},(index,text) -> {
                if (index == 0) {
                    ClipboardHelper.copyTextToClipboard(this,urlNavModel.getUrl());
                    showToast("网址已复制");
                }else {
                    Disposable disposable = urlNavDao.deleteNavUrlAt(urlNavModel.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(()-> {
                                navAdapter.remove(urlNavModel);
                                NavUrlChangeEvent event = new NavUrlChangeEvent(position);
                                EventBus.getDefault().post(event);
                            },Throwable::printStackTrace);
                    addDisposable(disposable);
                }
            });
        });

        urlNavDao = App.getApp().getAppDataBase().urlNavDao();

        Disposable disposable = urlNavDao.getNavInfoList(0,200)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(navAdapter::setNewInstance,Throwable::printStackTrace);
        addDisposable(disposable);
    }

    @OnClick(R.id.back_im)
    public void backAction() {
        finish();
    }
}
