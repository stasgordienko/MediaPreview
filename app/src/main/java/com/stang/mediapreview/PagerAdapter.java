package com.stang.mediapreview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Stanislav on 22.11.2016.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = MainActivity.TAG;

    int mNumOfTabs;
    public PhotoPrevFragment photoTab;
    public VideoPrevFragment videoTab;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        this.mNumOfTabs = 2;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                photoTab = new PhotoPrevFragment();
                return photoTab;
            case 1:
                videoTab = new VideoPrevFragment();
                return videoTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


