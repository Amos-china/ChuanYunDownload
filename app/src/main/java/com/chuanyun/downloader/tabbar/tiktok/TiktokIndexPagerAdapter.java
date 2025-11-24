package com.chuanyun.downloader.tabbar.tiktok;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.chuanyun.downloader.base.fragment.LazyLoadFragment;

import java.util.List;

public class TiktokIndexPagerAdapter extends FragmentStateAdapter {
    private final List<LazyLoadFragment> fragments;

    public TiktokIndexPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                            List<LazyLoadFragment> fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    public TiktokIndexPagerAdapter(@NonNull Fragment fragment, List<LazyLoadFragment> fragments) {
        super(fragment);
        this.fragments = fragments;
    }

    public TiktokIndexPagerAdapter(@NonNull FragmentManager fragmentManager,
                            @NonNull Lifecycle lifecycle,
                            List<LazyLoadFragment> fragments) {
        super(fragmentManager, lifecycle);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public LazyLoadFragment getFragment(int position) {
        return fragments.get(position);
    }
}
