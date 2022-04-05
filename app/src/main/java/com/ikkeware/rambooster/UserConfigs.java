package com.ikkeware.rambooster;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;

import CustomComponents.FancyGifDialog;
import CustomComponents.FancyGifDialogListener;

import static android.content.Context.MODE_PRIVATE;
import static com.ikkeware.rambooster.model.AppTheme.getCurrentApplicationTheme;

public class UserConfigs {
    Context context;
    public final String RATTING_DIALOG_CONFIG="ratting_dialog";
    public final  String USER_CONFIG="user.config";
    public final String AMOUNT_TIME_SHOWED="amount_time_showed";
    public final String RATED_APP="rated_app";
    public final String TUTORIAL_DIALOG_CONFIG="tutorial_dialog_config";
    public final String IS_FIRST_TIME="is_first_time_use_shortcuts";
    public final String IS_FIRST_TIME_TIP_7="is_first_time_tip";

    UserConfigs(Context context){
        this.context=context;
    }


    public void showRattingDialog(){

        new FancyGifDialog.Builder(context)
                .setTitle("Love Speed Up?")
                .setTitleColor((getCurrentApplicationTheme()==R.style.DarkAppTheme)? Color.WHITE:Color.BLACK)
                .setTitleFontStyle(Typeface.BOLD)
                .setMessage("Are you enjoying Speed Up ?! We'd love to know your opinion.\n Rate us on Google Play!")
                .setMessageSize(15)
                .setGifResource(R.drawable.rate_us_header)
                .setPositiveBtnText("Rate")
                .setPositiveBtnBackground("#A227FF")
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences sharedPreferences=context.getSharedPreferences(USER_CONFIG+"."+RATTING_DIALOG_CONFIG,MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean(RATED_APP,true);
                        editor.apply();
                        String url="https://play.google.com/store/apps/details?id="+context.getPackageName();
                        Intent rate=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(rate);
                    }
                })
                .setNegativeBtnBackground("#C0C0C0")
                .setNegativeBtnText("Not now")
                .isCancellable(true)
                .build();

    }





}
