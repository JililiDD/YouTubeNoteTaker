package com.example.dingdang.youtubenotetaker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Dingdang on 2/22/2018.
 */

// This is the adapter for the tabview. Referenced from https://www.youtube.com/watch?v=zcnT-3F-9JA
public class PagerAdapter extends FragmentStatePagerAdapter{

    int mNoOfTabs;

    public PagerAdapter (FragmentManager fm, int numOfTabs){
        super(fm);
        this.mNoOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                VideoSearchFragment searchMode = new VideoSearchFragment();
                return searchMode;
            case 1:
                NoteModeFragment noteMode = new NoteModeFragment();
                return noteMode;
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return 0;
    }

}
