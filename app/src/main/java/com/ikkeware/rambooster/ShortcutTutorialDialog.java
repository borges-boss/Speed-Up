package com.ikkeware.rambooster;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ShortcutTutorialDialog extends DialogFragment {

    SliderPageAdapter adapter;
    Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.shortcut_tutorial_layout,container);
        final ViewPager viewPager=view.findViewById(R.id.pagerIntroSlider);
        final Button btnNextTabs=view.findViewById(R.id.btnNextTabs);
        TabLayout tabLayout=view.findViewById(R.id.tabs);

        adapter=new SliderPageAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        //set adapter
        viewPager.setAdapter(adapter);

        //set dot indicator
        tabLayout.setupWithViewPager(viewPager);

        btnNextTabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() < adapter.getCount()) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position==adapter.getCount()-1){
                    btnNextTabs.setText("Got it");
                    btnNextTabs.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           dismiss();
                        }
                    });
                }
                else{
                    btnNextTabs.setText("Next");
                    btnNextTabs.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (viewPager.getCurrentItem() < adapter.getCount()) {
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            }
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setLayout(width, height);
        }

    }
}
