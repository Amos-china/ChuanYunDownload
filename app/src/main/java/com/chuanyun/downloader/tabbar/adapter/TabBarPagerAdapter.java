package com.chuanyun.downloader.tabbar.adapter;

import android.content.Context;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;


import java.util.List;

public class TabBarPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private Context context;
    private List<Fragment> fragmentList;

    public TabBarPagerAdapter(@NonNull FragmentManager fragmentManager,
                              @NonNull List<Fragment> fragmentList,
                              int behavior) {
        super(fragmentManager,behavior);
        this.fragmentList = fragmentList;
        this.fragmentManager = fragmentManager;
    }

    public TabBarPagerAdapter(@NonNull FragmentManager fragmentManager,
                              @NonNull List<Fragment> fragmentList,
                              Context context) {
        super(fragmentManager);
        this.fragmentList = fragmentList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentList.get(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fragment);
        fragmentTransaction.commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = fragmentList.get(position);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

}
