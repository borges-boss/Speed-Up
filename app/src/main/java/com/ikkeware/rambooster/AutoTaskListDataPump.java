package com.ikkeware.rambooster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ikkeware.rambooster.model.AppTheme.getCurrentApplicationTheme;

class AutoTaskListDataPump {

    AutoTaskSetting autoTaskSetting;


    public AutoTaskListDataPump(AutoTaskSetting autoTaskSetting){

        if(autoTaskSetting!=null){
            this.autoTaskSetting=autoTaskSetting;
        }
        else{
            this.autoTaskSetting=new AutoTaskSetting(0,0,0,0,0,0,0,0,0,0,0,0);
        }


    }

    public AutoTaskListDataPump(){

    }


    HashMap<String, List<AutoTaskItem>> getData(){
        HashMap<String,List<AutoTaskItem>> data=new HashMap<>();
        List<AutoTaskItem> networkSettings = new ArrayList<>();
        List<AutoTaskItem> display = new ArrayList<>();
        List<AutoTaskItem> volumeSettings = new ArrayList<>();
        List<AutoTaskItem> callsandnotifications = new ArrayList<>();
        List<AutoTaskItem> optimizationsSettings= new ArrayList<>();

        if(getCurrentApplicationTheme()==R.style.LightAppTheme) {

            optimizationsSettings.add(new AutoTaskItem("Clear Background Apps", R.drawable.ic_rocket,0));
            data.put("Optimizations",optimizationsSettings);


            networkSettings.add(new AutoTaskItem("Enable Wi-Fi", R.drawable.wifi,autoTaskSetting.getWifi_enable()));
            //    networkSettings.add(new AutoTaskItem("Mobile Data", R.drawable.data,autoTaskSetting.getEnable_mobile_data()));
            networkSettings.add(new AutoTaskItem("Bluetooth", R.drawable.bluetooth,autoTaskSetting.getEnable_bluetooth()));
            // networkSettings.add(new AutoTaskItem("GPS Location", R.drawable.location,autoTaskSetting.getEnable_gps()));
            data.put("Wi-Fi and Networks", networkSettings);


            display.add(new AutoTaskItem("Brightness", R.drawable.brightness_icon,autoTaskSetting.getBrightness()));
            data.put("Display", display);

            volumeSettings.add(new AutoTaskItem("Media volume", R.drawable.media_volume,autoTaskSetting.getMedia_volume()));
            volumeSettings.add(new AutoTaskItem("Call volume", R.drawable.call_volume,autoTaskSetting.getCall_volume()));
            volumeSettings.add(new AutoTaskItem("Ring volume", R.drawable.ring_volume,autoTaskSetting.getRing_volume()));
            data.put("Volume and sounds", volumeSettings);


            //callsandnotifications.add(new AutoTaskItem("Block calls", R.drawable.call_volume,autoTaskSetting.getEnable_block_call()));
            callsandnotifications.add(new AutoTaskItem("Block notifications", R.drawable.notification_block,autoTaskSetting.getEnable_notification_block()));
            data.put("Calls and Notifications", callsandnotifications);

        }
        else{

            optimizationsSettings.add(new AutoTaskItem("Clear Background Apps", R.drawable.ic_rocket_white,0));
            data.put("Optimizations",optimizationsSettings);

            networkSettings.add(new AutoTaskItem("Enable Wi-Fi", R.drawable.wifi_dark,autoTaskSetting.getWifi_enable()));
            //  networkSettings.add(new AutoTaskItem("Mobile Data", R.drawable.data_dark,autoTaskSetting.getEnable_mobile_data()));
            networkSettings.add(new AutoTaskItem("Bluetooth", R.drawable.bluetooth_dark,autoTaskSetting.getEnable_bluetooth()));
            //   networkSettings.add(new AutoTaskItem("GPS Location", R.drawable.location_dark,autoTaskSetting.getEnable_gps()));
            data.put("Wi-Fi and Networks", networkSettings);


            display.add(new AutoTaskItem("Brightness", R.drawable.brightness_icon_dark,autoTaskSetting.getBrightness()));
            data.put("Display", display);

            volumeSettings.add(new AutoTaskItem("Media volume", R.drawable.media_volume_dark,autoTaskSetting.getMedia_volume()));
            volumeSettings.add(new AutoTaskItem("Call volume", R.drawable.call_volume_dark,autoTaskSetting.getCall_volume()));
            volumeSettings.add(new AutoTaskItem("Ring volume", R.drawable.ring_volume_dark,autoTaskSetting.getRing_volume()));
            data.put("Volume and sounds", volumeSettings);


            //callsandnotifications.add(new AutoTaskItem("Block calls", R.drawable.call_volume_dark,autoTaskSetting.getEnable_block_call()));
            callsandnotifications.add(new AutoTaskItem("Block notifications", R.drawable.notification_block_dark,autoTaskSetting.getEnable_notification_block()));
            data.put("Calls and Notifications", callsandnotifications);

        }





        return data;
    }

    List<AutoTaskHeader> getTitles(){
        List<AutoTaskHeader> list=new ArrayList<>();
        list.add(new AutoTaskHeader(R.drawable.ic_cpu,"Optimizations",R.drawable.auto_task_header_optimizations));
        list.add(new AutoTaskHeader(R.drawable.wifi_icon,"Wi-Fi and Networks",R.drawable.auto_task_header_background_purple));
        list.add(new AutoTaskHeader(R.drawable.display_brightness_header,"Display",R.drawable.auto_task_header_background_display));
        list.add(new AutoTaskHeader(R.drawable.volume_icon,"Volume and sounds", R.drawable.auto_task_header_background_volume));
        list.add(new AutoTaskHeader(R.drawable.notifications_icon,"Calls and Notifications",R.drawable.auto_task_header_background_calls));

        return list;
    }







}
