package com.ikkeware.rambooster.model;

import android.graphics.drawable.Drawable;
import android.view.View;

public class AppDetail {

    private String appName="", packName="";
    private Drawable appIconImage;
    private View.OnClickListener listener,autotaskListener;

    public AppDetail(String appName, Drawable appIconImage,String packName){
        this.appIconImage=appIconImage;
        this.appName=appName;
        this.packName=packName;

    }

    public AppDetail(String appName, Drawable appIconImage, String packName, View.OnClickListener listener,View.OnClickListener autotaskListener){
        this.appIconImage=appIconImage;
        this.appName=appName;
        this.packName=packName;
        this.listener=listener;
        this.autotaskListener=autotaskListener;
    }


    public String getAppName(){ return appName;}

    public void setAppName(String appName){ this.appName=appName; }

    public Drawable getAppIconImage(){ return appIconImage;}

    public String getPackName() {
        return packName;
    }

    public View.OnClickListener getListener(){return listener; }

    public View.OnClickListener getAutotaskListener(){return autotaskListener; }
}
