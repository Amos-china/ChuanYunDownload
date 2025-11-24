package com.chuanyun.downloader.tabbar.fragmentManager;

import android.util.SparseArray;

import androidx.fragment.app.Fragment;

import com.chuanyun.downloader.tabbar.download.ui.DownloadIndexFragment;
import com.chuanyun.downloader.tabbar.home.ui.HomeIndexFragment;
import com.chuanyun.downloader.tabbar.me.ui.MeIndexFragment;
import com.chuanyun.downloader.tabbar.tiktok.TiktokIndexFragment;
import com.chuanyun.downloader.tabbar.tiktok.TiktokVideoFragment;
import com.chuanyun.downloader.tabbar.torrent.ui.TorrentIndexFragment;

public class MainFragmentFactory {
    public static final int MAIN_FRAGMENT_0 = 0;
    public static final int MAIN_FRAGMENT_1 = 1;
    public static final int MAIN_FRAGMENT_2 = 2;
    public static final int MAIN_FRAGMENT_3 = 3;
    public static final int MAIN_FRAGMENT_4 = 4;


    public static SparseArray<Fragment> fragments = new SparseArray<>();


    public static Fragment createFragment(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment != null) {
            return fragment;
        }
        switch (position) {
            case MAIN_FRAGMENT_0:
                fragment = new DownloadIndexFragment();
                fragments.put(MAIN_FRAGMENT_0, fragment);
                break;
            case MAIN_FRAGMENT_1:

                fragment = new TorrentIndexFragment();

                fragments.put(MAIN_FRAGMENT_1, fragment);
                break;
            case MAIN_FRAGMENT_2:


                fragment = new HomeIndexFragment();
                fragments.put(MAIN_FRAGMENT_2, fragment);
                break;
            case MAIN_FRAGMENT_3:

                fragment = new TiktokVideoFragment();
                fragments.put(MAIN_FRAGMENT_3, fragment);
                break;
            case MAIN_FRAGMENT_4:
                fragment = new MeIndexFragment();
                fragments.put(MAIN_FRAGMENT_4, fragment);
                break;
        }
        return fragment;
    }
}
