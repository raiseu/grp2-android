package com.example.mdpgrp2;

import android.content.Context;
import android.util.AndroidException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class bottomSheetAdapters extends FragmentPagerAdapter {

  private Context mycontext;
  int totalTabs;

    public bottomSheetAdapters(Context context, FragmentManager fm ,int totalTabs) {
        super(fm);
        mycontext = context;
        this.totalTabs = totalTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                FastestPathTimerFragment FPTFragment = new FastestPathTimerFragment();
                return FPTFragment;

            case 1:
                ImgRecTimerFragment IRTFragment = new ImgRecTimerFragment();
                return IRTFragment;

            default:
                return null;

        }

    }

    // count total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
