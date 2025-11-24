package com.chuanyun.downloader.tabbar.tiktok;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;

import androidx.viewpager2.widget.ViewPager2;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.fragment.BaseLazyFragment;
import com.chuanyun.downloader.base.fragment.LazyLoadFragment;
import com.chuanyun.downloader.base.view.ViewPager2Helper;

import net.lucode.hackware.magicindicator.MagicIndicator;
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

public class TiktokIndexFragment extends BaseLazyFragment {

    @BindView(R.id.sort_vp)
    ViewPager2 sortVp;

    @BindView(R.id.magic_view)
    MagicIndicator magicIndicator;


    private TiktokIndexPagerAdapter tabBarPagerAdapter;
    private CommonNavigatorAdapter navigatorAdapter;

    private List<TiktokSortModel> titleList = new ArrayList<>();
    private List<LazyLoadFragment> fragmentList = new ArrayList<>();

    @Override
    protected int setContentView() {
        return R.layout.fragment_tiktok_index;
    }

    @Override
    public void onPause() {
        super.onPause();

        setStatusBarTextColor(true);

        for (int i = 0; i < fragmentList.size(); i ++) {
            TiktokVideoFragment videoFragment = (TiktokVideoFragment) fragmentList.get(i);
            videoFragment.stopPlay();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setStatusBarTextColor(false);
    }

    @Override
    protected void initViews() {
        setStateBarHeight();

        tabBarPagerAdapter = new TiktokIndexPagerAdapter(getChildFragmentManager(),getLifecycle(),fragmentList);
        sortVp.setAdapter(tabBarPagerAdapter);
        sortVp.setOffscreenPageLimit(1);

        initMagicIndicator();
    }

    @Override
    protected void lazyLoad() {
        createData();
    }

    private void createData() {
//        String[] titles = new String[]{"随机视频","气质美女","热辣舞蹈","魅力女神","国风次元","丝袜美腿","高跟美脚","街拍展会","健身瑜伽"};
        String[] titles = new String[]{"随机视频"};
        for (int i = 0; i < titles.length; i ++ ) {
            TiktokSortModel sortModel = new TiktokSortModel(titles[i],i);
            TiktokVideoFragment videoFragment = new TiktokVideoFragment();
            videoFragment.setSortModel(sortModel);
            titleList.add(sortModel);
            fragmentList.add(videoFragment);
        }
        reloadData();
    }

    private void reloadData() {
        tabBarPagerAdapter.notifyDataSetChanged();
        navigatorAdapter.notifyDataSetChanged();
    }

    private void initMagicIndicator() {

        CommonNavigator commonNavigator = new CommonNavigator(getContext());

        commonNavigator.setAdjustMode(false);

        navigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(titleList.get(index).getTitle());
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setTypeface(null, Typeface.BOLD);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.color_f2f2f2));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.white));
                simplePagerTitleView.setText(titleList.get(index).getTitle());
                simplePagerTitleView.setOnClickListener(v -> {
                    sortVp.setCurrentItem(index);
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
                indicator.setColors(getResources().getColor(R.color.white));
                return indicator;
            }
        };

        commonNavigator.setAdapter(navigatorAdapter);
        magicIndicator.setNavigator(commonNavigator);
        ViewPager2Helper.bind(magicIndicator, sortVp);

    }


    private void setStatusBarTextColor(boolean light) {
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            View decorView = window.getDecorView();
            int flags = decorView.getSystemUiVisibility();

            if (light) {
                // 设置浅色模式（白色字体）
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                // 设置深色模式（黑色字体）
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }

            decorView.setSystemUiVisibility(flags);
        }
    }
}
