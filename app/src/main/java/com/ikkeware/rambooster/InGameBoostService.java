package com.ikkeware.rambooster;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class InGameBoostService extends Service {

    int cleaningMode;
    int totalInstalledApps;
    final String CHANNEL_ID="CLEANER_NOTIFICATION";
    String pkgName;
    CountDownTimer countDownTimer;
    @Nullable
    @Override

    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopSelf();
        return super.onUnbind(intent);
    }

    private void showNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "RAM Cleaner Notification", importance);
            channel.setDescription("Background app cleaner notification");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        ServiceKiller serviceKiller=new ServiceKiller();

        IntentFilter filter=new IntentFilter();
        filter.addAction("com.ikkeware.ServiceKiller");
        registerReceiver(serviceKiller,filter);

        Intent killService = new Intent();
        killService.setAction("com.ikkeware.ServiceKiller");

        PendingIntent killServicePendingIntent=PendingIntent.getBroadcast(this,0,killService,0);
        String content=(cleaningMode>=0 && cleaningMode<50)?"Low boost":(cleaningMode>=50 && cleaningMode<100)?"Medium boost":(cleaningMode==100)?"Ultra boost":"Low Cleaning";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("RAM Booster running...").setContentText(content+" active")
                .setPriority(NotificationCompat.PRIORITY_HIGH).setSmallIcon(R.drawable.lightning_icon)
                .setOngoing(true).setAutoCancel(true).setContentIntent(killServicePendingIntent);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        assert notificationManager != null;
        notificationManager.notify(7, builder.build());
    }

    private int getTotalNumberOfInstalledApps(){
        PackageManager pkg=getPackageManager();
        int total=0;
        //Getting non system apps only
        for(ApplicationInfo appinfo:pkg.getInstalledApplications(PackageManager.GET_META_DATA)) {
            if (pkg.getLaunchIntentForPackage(appinfo.packageName) != null && !appinfo.packageName.equals(getPackageName())) {
                total++;
            }
        }

        return total;
    }

    private void killBackgroundProcessInGame(int mode){
        PackageManager pkg=getPackageManager();
        ActivityManager am=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int i=0;
        assert am != null;
        int limit=0;

        if(cleaningMode>=0 && cleaningMode<50){
            limit=totalInstalledApps/3;
        }
        else if(cleaningMode>=50 && cleaningMode<100){
            limit=totalInstalledApps/2;
        }
        else if(cleaningMode==100){
            limit=totalInstalledApps;
        }

        for(ApplicationInfo appinfo:pkg.getInstalledApplications(PackageManager.GET_META_DATA)){

            //Verifica se o app é não é do sistema
            if(pkg.getLaunchIntentForPackage(appinfo.packageName)!=null && !appinfo.packageName.equals(getPackageName())
                    && !appinfo.packageName.equals(pkgName)){
                if(i==limit){
                    break;
                }
                else {
                    am.killBackgroundProcesses(appinfo.packageName);
                    i++;
                }
            }
        }

        countDownTimer=new CountDownTimer(5000,20) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                killBackgroundProcessInGame(cleaningMode);
            }

        };

        countDownTimer.start();


    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pkgName=intent.getStringExtra("pkgName");
        cleaningMode=intent.getIntExtra("mode",0);
        totalInstalledApps=getTotalNumberOfInstalledApps();
        showNotification();
        killBackgroundProcessInGame(intent.getIntExtra("mode",0));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLowMemory() {
        killBackgroundProcessInGame(cleaningMode);
        super.onLowMemory();
    }


    public class ServiceKiller extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();

        }
    }


}

