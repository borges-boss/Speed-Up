package com.ikkeware.rambooster;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;


import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class ShortcutController extends Activity {
    public ShortcutController(){


    }


    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String packageForLaunch= getIntent().getStringExtra("packageNameForLaunch");

        assert packageForLaunch != null;
        Intent intent=getPackageManager().getLaunchIntentForPackage(packageForLaunch);

        if(intent!=null){
            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
           SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date=new Date();
            MainActivity.ApplySettings applySettings=new MainActivity.ApplySettings(packageForLaunch,getApplicationContext(),dateFormat.format(date));
            applySettings.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
        else{
            intent=new Intent(ShortcutController.this,MainActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"Application not found :(",Toast.LENGTH_LONG).show();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                ShortcutManager shortcutManager=getSystemService(ShortcutManager.class);
                assert shortcutManager != null;
                shortcutManager.removeDynamicShortcuts(Arrays.asList(packageForLaunch));//packageForLaunch is the shortcut id
                shortcutManager.getPinnedShortcuts();
                shortcutManager.disableShortcuts(Arrays.asList(packageForLaunch));
            }

        }

        finish();


    }
}
