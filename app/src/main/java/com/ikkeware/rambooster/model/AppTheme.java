package com.ikkeware.rambooster.model;

import com.ikkeware.rambooster.R;

public  class AppTheme {
    //@{teste.isDarkTheme? @drawable/settings_individual_black: @drawable/settings_individual}
    private static int theme= R.style.LightAppTheme;


    public AppTheme(int theme){
        AppTheme.theme =theme;
    }

    public static int getCurrentApplicationTheme(){
        return AppTheme.theme;
    }

    public static void setApplicationTheme(int theme){ AppTheme.theme=theme; }



}
