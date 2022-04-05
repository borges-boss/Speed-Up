package com.ikkeware.rambooster;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;



public class RattingDialogJob extends JobService {


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        UserConfigs configs=new UserConfigs(getApplicationContext());
        SharedPreferences sharedPreferences=getSharedPreferences(configs.RATTING_DIALOG_CONFIG+"."+configs.RATTING_DIALOG_CONFIG,MODE_PRIVATE);

        if(!sharedPreferences.getBoolean("rated_app",false)){
            configs.showRattingDialog();
        }
        else{
            stopSelf();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) { return false; }
}
