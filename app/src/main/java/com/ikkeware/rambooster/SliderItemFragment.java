package com.ikkeware.rambooster;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public class SliderItemFragment extends Fragment {

    private static final String ARG_POSITION = "slider-position";
    // prepare all title ids arrays
    @StringRes
    private static final int[] PAGE_TITLES =
            new int[] { R.string.title_main_tutorial,R.string.title_tutorial8, R.string.title_tutorial7_1};

    // prepare all subtitle ids arrays
    @StringRes
    private static final int[] PAGE_TEXT =
            new int[] {
                    R.string.text_main_tutorial,R.string.text_page8,R.string.text_page7_1
            };
    // prepare all subtitle images arrays
    @StringRes
    private static final int[] PAGE_IMAGE =
            new int[] {
                    R.drawable.tutorial_img,R.drawable.test_tutorial_android8,R.drawable.moon
            };

    // prepare all background images arrays
    /*
    @StringRes
    private static final int[] BG_IMAGE = new int[] {
            R.drawable.ic_bg_red, R.drawable.ic_bg_blue, R.drawable.ic_bg_green,
            R.drawable.ic_bg_purple
    };
    */

    private int position;

    public SliderItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     *
     * @return A new instance of fragment SliderItemFragment.
     */

    public static SliderItemFragment newInstance(int position) {
        SliderItemFragment fragment = new SliderItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slider_item, null, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set page background
        //view.setBackground(requireActivity().getDrawable(BG_IMAGE[position]));

        TextView title = view.findViewById(R.id.textView);
        TextView titleText = view.findViewById(R.id.textView2);
        ImageView imageView = view.findViewById(R.id.imageView);

        // set page title
        title.setText(PAGE_TITLES[position]);
        // set page sub title text
        titleText.setText(PAGE_TEXT[position]);
        // set page image
        imageView.setImageResource(PAGE_IMAGE[position]);
    }

    @Override
    public void onStart() {
        super.onStart();


    }
}
