package com.ikkeware.rambooster;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SliderPageAdapter extends FragmentPagerAdapter {


    public SliderPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }
    @NonNull
    @Override public Fragment getItem(int position) {
        return SliderItemFragment.newInstance(position);
    }
    // size is hardcoded
    @Override public int getCount() {
        return 3;
    }

}
