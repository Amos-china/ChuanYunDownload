package com.chuanyun.downloader.tabbar.fragmentManager;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class TTFragmentManager {

    private List<Fragment> fragmentList;
    private final FragmentManager fragmentManager;
    private final int containerId;

    private int currentSelectPosition = 0;

    public TTFragmentManager(FragmentManager fragmentManager, List<Fragment> fragmentList, @IdRes int containerId) {
        this.fragmentList = fragmentList;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }


    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    public int getFragmentCount() {
        return fragmentList.size();
    }


    public int getCurrentSelectPosition() {
        return currentSelectPosition;
    }

    /**
     * 切换到指定的Fragment。
     *
     * @param position 要显示的index
     */
    public void switchFragment(int position) {
        Fragment fragment = getItem(position);
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        List<Fragment> fragmentList=fragmentManager.getFragments();
        for (Fragment fragment1 : fragmentList) {
            if (fragment1.isAdded()){
                transaction.hide(fragment1);
            }
        }

        if (!fragment.isAdded()){
            transaction.add(containerId, fragment, tag);
        }else {
            transaction.show(fragment);
        }

        transaction.commit();

        currentSelectPosition = position;
    }
}
