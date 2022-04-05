package com.ikkeware.rambooster;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.core.app.NotificationCompat;



public class NotificationListener extends NotificationListenerService {
    final String CHANNEL_ID="UNIQUE_NOTIFICATION_BLOCKER";
    private NLServiceReceiver nlServiceReceiver;
    ServiceKiller serviceKiller;
    public static boolean isListenerActive=false;


    @Override
    public void onCreate() {
        super.onCreate();
        nlServiceReceiver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.ikkeware.rambooster.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlServiceReceiver, filter);
        //Toast.makeText(getApplicationContext(),"Created",Toast.LENGTH_LONG).show();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = getString(R.string.notification_channel);
            // String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("Notification Blocker Notification");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceKiller=new ServiceKiller();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.ikkeware.ServiceKiller");
        registerReceiver(serviceKiller,filter);

        Intent killService = new Intent();
        killService.setAction("com.ikkeware.ServiceKiller");

        isListenerActive=intent.getBooleanExtra("isListenerActive",false);

        PendingIntent killServicePendingIntent=PendingIntent.getBroadcast(this,0,killService,0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Notification Blocker Running...").setContentText("Tap here to finish the notification blocker")
                .setPriority(NotificationCompat.PRIORITY_MAX).setSmallIcon(R.drawable.lightning_icon)
                .setOngoing(true).setAutoCancel(true).setContentIntent(killServicePendingIntent);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        assert notificationManager != null;
        notificationManager.notify(11, builder.build());


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlServiceReceiver);
//        unregisterReceiver(serviceKiller);
        stopSelf();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if(isListenerActive) {
            Intent i = new Intent("com.ikkeware.rambooster.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("notification_event", "onNotificationPosted :" + sbn.getPackageName() + "\n");
            sendBroadcast(i);

            //cancelNotification(sbn.getKey());
        }


    }


    class NLServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            if (isListenerActive) {

                NotificationListener.this.cancelAllNotifications();

/*
                Intent i1 = new Intent("com.ikkeware.rambooster.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
                i1.putExtra("notification_event", "=====================");
                sendBroadcast(i1);
                int i = 1;
                for (StatusBarNotification sbn : NotificationListener.this.getActiveNotifications()) {
                    Intent i2 = new Intent("com.ikkeware.rambooster.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
                    i2.putExtra("notification_event", i + " " + sbn.getPackageName() + "\n");
                    sendBroadcast(i2);
                    System.err.println("NOTIFICATION CONTENT:" + sbn.getNotification().tickerText);
                    i++;
                }
                Intent i3 = new Intent("com.ikkeware.rambooster.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
                i3.putExtra("notification_event", "===== Notification List ====");
                sendBroadcast(i3);




            }
            */
            }
        }


    }

    class ServiceKiller extends BroadcastReceiver{


        private boolean isServiceRunning(Class<?> serviceClass){

            ActivityManager ac= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            assert ac != null;
            for(ActivityManager.RunningServiceInfo info:ac.getRunningServices(Integer.MAX_VALUE)){
                System.err.println("teste:"+info.service.getPackageName());
                if(serviceClass.getName().equals(info.service.getClassName())){
                    return true;
                }


            }
            return false;
        }

        //Pause the Listener
        private void pauseService(){
            isListenerActive=false;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            pauseService();
        }
    }



}


