package com.chuanyun.downloader.tabbar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.view.ControlScrollViewPager;
import com.chuanyun.downloader.tabbar.model.TabBarItemModel;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.List;

public class TabBarAdapter extends CommonNavigatorAdapter {
    private List<TabBarItemModel> tabBarItemModelList;
    private ControlScrollViewPager viewPager;

    private TabBarSelectIndexListener tabBarSelectIndexListener;

    public interface TabBarSelectIndexListener {
        void onSelected(int index);
    }

    public void setTabBarSelectIndexListener(TabBarSelectIndexListener tabBarSelectIndexListener) {
        this.tabBarSelectIndexListener = tabBarSelectIndexListener;
    }

    public TabBarAdapter(List<TabBarItemModel> tabBarItemModelList, ViewPager viewPager) {
        this.tabBarItemModelList = tabBarItemModelList;
        this.viewPager = (ControlScrollViewPager) viewPager;
    }

    @Override
    public int getCount() {
        return tabBarItemModelList.size();
    }

    @Override
    public IPagerTitleView getTitleView(Context context, int index) {
        CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);


        View customLayout = LayoutInflater.from(context).inflate(R.layout.tabbar_item, null);
        final ImageView titleImg = (ImageView) customLayout.findViewById(R.id.title_img);
        final TextView titleText = (TextView) customLayout.findViewById(R.id.title_text);
        TabBarItemModel itemModel = tabBarItemModelList.get(index);
        titleImg.setImageResource(itemModel.getImageRes());
        titleText.setText(itemModel.getTitle());
        commonPagerTitleView.setContentView(customLayout);

        commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

            @Override
            public void onSelected(int index, int totalCount) {
                titleText.setTextColor(context.getResources().getColor(R.color.app_color));
                titleImg.setImageResource(tabBarItemModelList.get(index).getSelectImageRes());
                if (tabBarSelectIndexListener != null) {tabBarSelectIndexListener.onSelected(index);}
            }

            @Override
            public void onDeselected(int index, int totalCount) {
                titleText.setTextColor(context.getResources().getColor(R.color.color_7f7f7f));
                titleImg.setImageResource(tabBarItemModelList.get(index).getImageRes());
            }

            @Override
            public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
            }

            @Override
            public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {

            }
        });

        commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(index,false);
            }
        });

        return commonPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        return null;
    }
}
