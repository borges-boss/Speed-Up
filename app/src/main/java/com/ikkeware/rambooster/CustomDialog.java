package com.ikkeware.rambooster;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.logging.Logger;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CustomDialog extends DialogFragment {

    public static Context context;
    TextView txtCache;
    private TextView txtProcess, txtDots,txtOptmize;
    ProgressBar progressBar1,progressBar2;
    Handler handler;
    private Animation animation;
    BoostPhone boost;
    ImageView imgDone1,imgDone2;
    ObjectAnimator objectAnimation;
    Button btnCancel;

    public static CustomDialog display(FragmentManager fragmentManager){
        CustomDialog customDialog=new CustomDialog(context);
        customDialog.show(fragmentManager,TAG);

        return customDialog;
    }

    public CustomDialog(Context context){
        CustomDialog.context =context;
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setStyle(CustomDialog.STYLE_NORMAL,R.style.CustomFragmentDialogStyle);

    }


    @Override
    public void onStart(){
        super.onStart();

        Dialog dialog=getDialog();
        if(dialog!=null){
            int width=ViewGroup.LayoutParams.MATCH_PARENT;
            int height=ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width,height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            boost= new BoostPhone();
            boost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.custom_dialog_frame,container,false);
        btnCancel= view.findViewById(R.id.btnCancel);
        txtCache=view.findViewById(R.id.txtCache);
        txtProcess=view.findViewById(R.id.txtProcess);
        txtDots=view.findViewById(R.id.dots);
        txtOptmize=view.findViewById(R.id.txtOptmize);
        progressBar1=view.findViewById(R.id.progressBar);
        progressBar2=view.findViewById(R.id.progress2);
        imgDone1=view.findViewById(R.id.imgDone1);
        imgDone2=view.findViewById(R.id.imgDone2);
        handler=new Handler();
        animation=AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!boost.isCancelled()){ boost.cancel(true); }
                getDialog().cancel();

            }
        });

        objectAnimation =ObjectAnimator.ofObject(new AnimatedTextView((TextView) txtDots), "text", new TypeEvaluator<String>() {
            @Override
            public String evaluate(float fraction, String startValue, String endValue) {
                return (fraction < 0.5)? startValue:endValue;
            }

        },"",".","..","...");
        objectAnimation.setDuration(1500L);
        objectAnimation.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimation.setRepeatMode(ValueAnimator.RESTART);
        //objectAnimation.start();


        return view;
    }


    public  class BoostPhone extends  AsyncTask<Void,Void,Void> {


        private void killBackgroundProcess(){
            PackageManager pkg=context.getPackageManager();
            ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            assert am != null;

            for(ApplicationInfo appinfo:pkg.getInstalledApplications(PackageManager.GET_META_DATA)){

                //Verifica se o app é não é do sistema
                if(pkg.getLaunchIntentForPackage(appinfo.packageName)!=null && !appinfo.packageName.equals(context.getPackageName())){
                    am.killBackgroundProcesses(appinfo.packageName);
                    //System.err.println("process: "+appinfo.processName+" killed");
                }
            }

        }

        public void deleteCache(Context context) {
            try {
                File dir = context.getCacheDir();
                deleteDir(dir);
            } catch (Exception e) {}
        }

        public  boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                Log.v("Directory","isDirectory");
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    Log.v("child","child run ");
                    if (!success) {
                        return false;
                    }
                }
            }
            assert dir != null;
            return dir.delete();
        }

        private void postData(final String key,final boolean hasFinished){

            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (key){

                        case "cache":
                            if(!hasFinished) {
                                txtCache.animate().alpha(1.0f);
                                progressBar1.setVisibility(View.VISIBLE);
                                txtCache.startAnimation(animation);
                                progressBar1.startAnimation(animation);
                                Log.v("ANIM", "CACHE ANI");

                            }
                            else{
                                Log.v("ANIM", "CACHE ANI FINISHED");
                                progressBar1.setVisibility(View.GONE);
                                imgDone1.setVisibility(View.VISIBLE);
                            }
                            break;

                        case "process":
                            if(!hasFinished) {
                                txtProcess.animate().alpha(1.0f);
                                progressBar2.setVisibility(View.VISIBLE);
                                txtProcess.startAnimation(animation);
                                progressBar2.startAnimation(animation);
                                Log.v("ANIM", "PROCESS ANI");

                            }
                            else{
                                Log.v("ANIM", "PROCESS ANI FINISHED");
                                progressBar2.setVisibility(View.GONE);
                                imgDone2.setVisibility(View.VISIBLE);
                            }
                            break;

                        default:
                            if(hasFinished){
                                txtOptmize.setText("Optimized !");

                            }
                            break;

                    }

                }
            });



        }



        @Override
        protected Void doInBackground(Void... voids) {


            postData("cache",false);
            deleteCache(context);
            postData("cache",true);

            postData("process",false);
            killBackgroundProcess();
            postData("process",true);

            postData("",true);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cancel(true);


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


    }

    private class AnimatedTextView{
        private  final TextView textView;

        public AnimatedTextView(TextView textView){
            this.textView=textView;
        }

        public String getText(){ return this.textView.getText().toString(); }

        public void setText(String text){ this.textView.setText(text); }



    }

}
