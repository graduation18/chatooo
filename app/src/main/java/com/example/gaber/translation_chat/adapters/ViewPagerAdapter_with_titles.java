package com.example.gaber.translation_chat.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaber on 06/10/2018.
 */

public class ViewPagerAdapter_with_titles extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentList_title = new ArrayList<>();

    public ViewPagerAdapter_with_titles(FragmentManager manager) {
        super(manager);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentList_title.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentList_title.add(title);

    }

}