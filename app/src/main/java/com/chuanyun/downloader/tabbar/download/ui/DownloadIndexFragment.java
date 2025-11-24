package com.chuanyun.downloader.tabbar.download.ui;

import android.content.Context;
import android.graphics.Typeface;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.app.App;
import com.chuanyun.downloader.base.fragment.BaseLazyFragment;
import com.chuanyun.downloader.eventBusModel.ShowAddTorrentViewEvent;
import com.chuanyun.downloader.login.model.UserLoginManager;
import com.chuanyun.downloader.models.ApiIndexModel;
import com.chuanyun.downloader.tabbar.adapter.TabBarPagerAdapter;
import com.chuanyun.downloader.web.WebViewController;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DownloadIndexFragment extends BaseLazyFragment {

    @BindView(R.id.task_vp)
    ViewPager taskVp;

    @BindView(R.id.magic_view)
    MagicIndicator magicIndicator;

    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected int setContentView() {
        return R.layout.fragment_download_index;
    }

    @Override
    protected void initViews() {

        setStateBarHeight();

        titleList.add("下载中");
        titleList.add("已完成");

        for (int i = 0; i < 2; i++) {
            DownloadTaskFragment taskFragment = new DownloadTaskFragment();
            taskFragment.setTaskStatus(i + 1);
            fragmentList.add(taskFragment);
        }


        TabBarPagerAdapter tabBarPagerAdapter = new TabBarPagerAdapter(getActivity().getSupportFragmentManager(),fragmentList,getContext());
        taskVp.setAdapter(tabBarPagerAdapter);

        initMagicIndicator();


        checkShowNoticeView();
    }

    @Override
    protected void lazyLoad() {
        //插入一个对象

    }

    private void initMagicIndicator() {

        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(titleList.get(index));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setTypeface(null, Typeface.BOLD);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.gray_999));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.black));
                simplePagerTitleView.setText(titleList.get(index));
                simplePagerTitleView.setOnClickListener(v -> {
                    taskVp.setCurrentItem(index);
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 4));
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setRoundRadius(UIUtil.dip2px(context, 2));
                indicator.setColors(getResources().getColor(R.color.app_color));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, taskVp);

    }

    @OnClick(R.id.add_torrent_im)
    public void addTorrentImAction() {
        EventBus.getDefault().post(new ShowAddTorrentViewEvent());
    }

    @OnClick(R.id.more_im)
    public void moreImAction() {
        String[] strings;
        if (taskVp.getCurrentItem() == 0) {
            strings = new String[] {"全部暂停","全部下载","删除全部任务","删除全部任务和文件"};
        }else {
            strings = new String[] {"删除全部任务","删除全部任务和文件"};
        }

        showBottomSheet("",strings,(index, text) -> {
            if (taskVp.getCurrentItem() == 0) {
                DownloadTaskFragment taskFragment = (DownloadTaskFragment) fragmentList.get(0);
                if (index == 0) {
                    taskFragment.stopAllTask();
                }else if (index == 1) {
                    taskFragment.downloadAllTask();
                }else if (index == 2){
                    taskFragment.deleteAllTask(false);
                }else {
                    taskFragment.deleteAllTask(true);
                }
            }else {
                DownloadTaskFragment taskFragment = (DownloadTaskFragment) fragmentList.get(1);
                if (index == 0) {
                    taskFragment.deleteAllTask(false);
                }else {
                    taskFragment.deleteAllTask(true);
                }
            }
        });
    }

    private void checkShowNoticeView() {
        ApiIndexModel indexModel = App.getApp().getApiIndexModel();
        if (indexModel == null) {return;}
        if (indexModel.getSftcgg() == 0) { return; }
        long showNoticeTime = UserLoginManager.getNoticeShowTime();
        long currentTime = App.getApp().getCloudTime();
        long timeCount = currentTime - showNoticeTime;
        if (timeCount < indexModel.getTanctzjg()) { return; }

        Disposable disposable = Observable.interval(indexModel.getTancjg(), TimeUnit.SECONDS)
                .take(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    createNoticeView();
                },Throwable::printStackTrace);
        addDisposable(disposable);
    }

    private void createNoticeView() {
        ApiIndexModel indexModel = App.getApp().getApiIndexModel();
        String doneTitle = indexModel.getSftcgg() == 1 ? indexModel.getJiandan() : indexModel.getPudan();
        showAlertView(indexModel.getGgbt(),indexModel.getGgnr(),"取消",doneTitle,()->{
            UserLoginManager.setNoticeShowTime(App.getApp().getCloudTime());
        },() -> {
            UserLoginManager.setNoticeShowTime(App.getApp().getCloudTime());
            if (indexModel.getSftcgg() == 2) {
                WebViewController.loadWeb(getContext(),indexModel.getPudkurl(),indexModel.getPudan());
            }
        });
    }

}
