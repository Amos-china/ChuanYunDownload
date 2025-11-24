package com.chuanyun.downloader.tabbar.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.chuanyun.downloader.tabbar.fragmentManager.MainFragmentFactory;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final FragmentManager fm;

    public MainPagerAdapter(FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {//指定Fragmemt
        return MainFragmentFactory.createFragment(position);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Fragment instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.show(fragment);
        fragmentTransaction.commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = MainFragmentFactory.fragments.get(position);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (fragment != null) {
            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

}
