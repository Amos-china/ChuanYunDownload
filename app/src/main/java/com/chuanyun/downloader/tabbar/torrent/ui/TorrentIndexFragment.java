package com.chuanyun.downloader.tabbar.torrent.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.fragment.BaseLazyFragment;
import com.chuanyun.downloader.tabbar.adapter.TabBarPagerAdapter;

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

public class TorrentIndexFragment extends BaseLazyFragment {

    @BindView(R.id.torrent_vp)
    ViewPager taskVp;

    @BindView(R.id.magic_view)
    MagicIndicator magicIndicator;

    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected int setContentView() {
        return R.layout.fragment_torrent_index;
    }

    @Override
    protected void initViews() {

        setStateBarHeight();

        titleList.add("文件");
        titleList.add("收藏");

        for (int i = 0; i < 2; i++) {
            TorrentListFragment taskFragment = new TorrentListFragment();
            taskFragment.setIndexType(i + 1);
            fragmentList.add(taskFragment);
        }


        TabBarPagerAdapter tabBarPagerAdapter = new TabBarPagerAdapter(getActivity().getSupportFragmentManager(),fragmentList,getContext());
        taskVp.setAdapter(tabBarPagerAdapter);

        initMagicIndicator();
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

    @Override
    protected void lazyLoad() {


    }




    @OnClick(R.id.manager_tv)
    public void managerTvAction() {
        startActivity(new Intent(getContext(),ManagerTorrentActivity.class));
    }
}
