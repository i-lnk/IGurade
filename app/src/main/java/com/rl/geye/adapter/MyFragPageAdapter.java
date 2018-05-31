package com.rl.geye.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Nicky on 2016/8/24.
 */
public class MyFragPageAdapter extends FragmentPagerAdapter {


    private Fragment[] mFragments;
    private CharSequence[] mTitles;

    public MyFragPageAdapter(FragmentManager fm, Fragment[] fragments, CharSequence[] titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title = "";
        try {
            title = mTitles[position];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }
}
