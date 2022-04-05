package com.ikkeware.rambooster;

public class AutoTaskItem {

    private String itemName;
    private int itemImg;
    private int setting;


    public AutoTaskItem(String itemName,int itemImg,int setting){

        this.itemName=itemName;
        this.itemImg=itemImg;
        this.setting=setting;
    }

    public String getItemName(){
        return itemName;
    }

    public int getItemImg(){
        return itemImg;
    }


    public int getItemSetting() {
        return setting;
    }

    public void setItemSetting(int value){
        setting=value;
    }
}
