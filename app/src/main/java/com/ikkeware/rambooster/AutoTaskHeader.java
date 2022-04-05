package com.ikkeware.rambooster;


public class AutoTaskHeader {

    private int img;
    private String title;
    private int color;

    public AutoTaskHeader(int img, String title, int color){
        this.title=title;
        this.img=img;
        this.color=color;
    }


    public int getImg(){
        return img;
    }
    public String getTitle(){
        return title;
    }
    public int getColor(){
        return color;
    }



}
