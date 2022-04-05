package com.ikkeware.rambooster;

public class AutoTaskSetting {


    private int auto_task_id=0;
    private int is_active;
    private int wifi_enable;
    private int enable_mobile_data;
    private int enable_bluetooth;
    private int enable_gps;
    private int brightness;
    private int media_volume;
    private int call_volume;
    private int ring_volume;
    private int enable_block_call;
    private int enable_notification_block;
    private int is_global_setting;


    public AutoTaskSetting(int is_active,int wifi_enable,int enable_mobile_data,int enable_bluetooth,
                           int enable_gps, int brightness,int media_volume, int call_volume,
                           int ring_volume, int enable_block_call,int enable_notification_block,int is_global_setting){


        this.is_active = is_active;
        this.wifi_enable=wifi_enable;
        this.enable_mobile_data =enable_mobile_data;
        this.enable_bluetooth = enable_bluetooth;
        this.enable_gps = enable_gps;
        this.brightness = brightness;
        this.media_volume = media_volume;
        this.call_volume = call_volume;
        this.ring_volume = ring_volume;
        this.enable_block_call = enable_block_call;
        this.enable_notification_block = enable_notification_block;
        this.is_global_setting = is_global_setting;
    }

    public AutoTaskSetting(int auto_task_id,int is_active,int wifi_enable,int enable_mobile_data,int enable_bluetooth,
                           int enable_gps, int brightness,int media_volume, int call_volume,
                           int ring_volume, int enable_block_call,int enable_notification_block,int is_global_setting){

        this.auto_task_id=auto_task_id;
        this.is_active = is_active;
        this.wifi_enable=wifi_enable;
        this.enable_mobile_data =enable_mobile_data;
        this.enable_bluetooth = enable_bluetooth;
        this.enable_gps = enable_gps;
        this.brightness = brightness;
        this.media_volume = media_volume;
        this.call_volume = call_volume;
        this.ring_volume = ring_volume;
        this.enable_block_call = enable_block_call;
        this.enable_notification_block = enable_notification_block;
        this.is_global_setting = is_global_setting;
    }

    public AutoTaskSetting(){}


    public int getAutoTaskId(){
        return auto_task_id;
    }
    private Object setSettingType(int value){
        Object object;
        if(value<=1){

            if(value==1){ object=true; }
            else if(value==0){object=false; }
            else{ object=false; }
        }
        else{
            object=value;
        }

        return object;
    }


    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public void setIs_global_setting(int is_global_setting){
        this.is_global_setting=is_global_setting;
    }

    public int getWifi_enable() {
        return wifi_enable;
    }

    public void setWifi_enable(int wifi_enable) {
        this.wifi_enable = wifi_enable;
    }

    public int getEnable_mobile_data() {
        return enable_mobile_data;
    }

    public void setEnable_mobile_data(int enable_mobile_data) {
        this.enable_mobile_data = enable_mobile_data;
    }

    public int getEnable_bluetooth() {
        return enable_bluetooth;
    }

    public void setEnable_bluetooth(int enable_bluetooth) {
        this.enable_bluetooth = enable_bluetooth;
    }

    public int getEnable_gps() {
        return enable_gps;

    }

    public void setEnable_gps(int enable_gps) {
        this.enable_gps = enable_gps;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getMedia_volume() {
        return media_volume;
    }

    public void setMedia_volume(int media_volume) {
        this.media_volume = media_volume;
    }

    public int getCall_volume() {
        return call_volume;
    }

    public int getRing_volume() {
        return ring_volume;
    }

    public void setRing_volume(int ring_volume) {
        this.ring_volume = ring_volume;
    }

    public int getEnable_block_call() {
        return enable_block_call;
    }

    public int getEnable_notification_block() {
        return enable_notification_block;
    }

    public void setEnable_notification_block(int enable_notification_block) {
        this.enable_notification_block = enable_notification_block;
    }

    public int getIs_global_setting() {
        return is_global_setting;
    }
}
