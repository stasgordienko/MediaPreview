package com.stang.mediapreview.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.stang.mediapreview.ui.MainActivity;
import com.stang.mediapreview.ui.PhotoPrevFragment;
import com.stang.mediapreview.ui.VideoPrevFragment;

/**
 * Created by Stanislav on 22.11.2016.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = MainActivity.TAG;
    public static final int PHOTO_TAB = 0;
    public static final int VIDEO_TAB = 1;

    int mNumOfTabs;
    public PhotoPrevFragment photoTab;
    public VideoPrevFragment videoTab;

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mNumOfTabs = 2;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case PHOTO_TAB:
                photoTab = new PhotoPrevFragment();
                return photoTab;
            case VIDEO_TAB:
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


