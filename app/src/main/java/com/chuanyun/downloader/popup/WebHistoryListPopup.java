package com.chuanyun.downloader.popup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.lxj.xpopup.util.XPopupUtils;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.tabbar.adapter.TabBarPagerAdapter;
import com.chuanyun.downloader.web.WebHistoryListFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class WebHistoryListPopup extends TTBaseBottomPopupView {

    @BindView(R.id.magic_view)
    MagicIndicator magicIndicator;

    @BindView(R.id.history_vp)
    ViewPager historyVp;

    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    private WebHistoryListFragment.OnHistoryItemClickListener historyItemClickListener;


    public void setHistoryItemClickListener(WebHistoryListFragment.OnHistoryItemClickListener historyItemClickListener) {
        this.historyItemClickListener = historyItemClickListener;
    }

    public WebHistoryListPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_web_history_list;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        titleList.add("历史");
        titleList.add("收藏");

        for (int i = 0; i < 2; i++) {
            WebHistoryListFragment historyListFragment = new WebHistoryListFragment();
            historyListFragment.setIndex(i);
            if (i == 0) {
                historyListFragment.setHistoryItemClickListener(webHistoryInfo -> {
                    historyItemClickListener.selectItem(webHistoryInfo);
                    dismiss();
                });
            }else {
                historyListFragment.setCollectItemClickListener(webHistoryInfo -> {
                    historyItemClickListener.selectItem(webHistoryInfo);
                    dismiss();
                });
            }
            fragmentList.add(historyListFragment);
        }

        FragmentActivity activity = (FragmentActivity) getContext();
        TabBarPagerAdapter tabBarPagerAdapter = new TabBarPagerAdapter(activity.getSupportFragmentManager(),fragmentList,getContext());
        historyVp.setAdapter(tabBarPagerAdapter);

        initMagicIndicator();
    }

    @OnClick(R.id.clear_tv)
    public void clearAction() {
        WebHistoryListFragment historyListFragment = (WebHistoryListFragment) fragmentList.get(historyVp.getCurrentItem());
        historyListFragment.clearList();
    }

    private void initMagicIndicator() {

        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdjustMode(false);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(titleList.get(index));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.gray_999));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.black));
                simplePagerTitleView.setText(titleList.get(index));
                simplePagerTitleView.setOnClickListener(v -> {
                    historyVp.setCurrentItem(index);
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
                indicator.setColors(getResources().getColor(R.color.black));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, historyVp);
    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getScreenHeight(getContext()) * .7f);
    }

    @Override
    protected boolean onBackPressed() {
        return true;
    }

    @Override
    protected List<String> getInternalFragmentNames() {
        ArrayList<String> list = new ArrayList<>();
        list.add(WebHistoryListFragment.class.getSimpleName());
        return list;
    }
}
