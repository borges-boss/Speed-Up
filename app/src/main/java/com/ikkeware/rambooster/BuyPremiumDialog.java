package com.ikkeware.rambooster;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;


public class BuyPremiumDialog extends BottomSheetDialogFragment {
    private int layoutReference=0;
    private String price;
    int theme;



    public BuyPremiumDialog(int layoutReference,int theme){
        this.layoutReference=layoutReference;
        this.theme=theme;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BuyPremiumDialog.STYLE_NORMAL,theme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.premium_dialog_frame,container);
        getDialog().setContentView(view);

        if(theme==R.style.BottomSheetDialogTheme){
            TextView textView=view.findViewById(R.id.bottomSheetTitle);
            ImageView img=view.findViewById(R.id.diamond);
            textView.setTextColor(Color.parseColor("#9717F8"));
            img.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9717F8")));
        }
        else if(theme==R.style.BottomSheetDialogThemeDark){
            TextView textView=view.findViewById(R.id.bottomSheetTitle);
            ImageView img=view.findViewById(R.id.diamond);
            textView.setTextColor(Color.WHITE);
            img.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }


        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        int width,height;
        //Dialog must fill 74% of the screen in width
        height = ViewGroup.LayoutParams.WRAP_CONTENT;

        width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(width,height);





    }
}
