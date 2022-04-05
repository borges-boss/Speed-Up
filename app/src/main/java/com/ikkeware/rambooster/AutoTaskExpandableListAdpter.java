package com.ikkeware.rambooster;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;

import com.ikkeware.rambooster.model.DataBase;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.HashMap;
import java.util.List;

import CustomComponents.FancyGifDialog;
import CustomComponents.FancyGifDialogListener;


public class AutoTaskExpandableListAdpter extends BaseExpandableListAdapter {

    private Context context;
    private HashMap<String, List<AutoTaskItem>> expandableListItems;
    private List<AutoTaskHeader> expandableListTitles;
    private ExpandableListView expandableListView;
    private HashMap<Integer,Float> angles= new HashMap<>();
    private String appPkgName;
    private boolean isLocked;
    private  HashMap<Integer,Integer> groupsToClose=new HashMap<>();
    public Activity thisActivity;
    private final int CALL_PHONE_CODE=454;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    public Boolean VerifyNotificationPermission(Context context) {
        String theList = android.provider.Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if(theList!=null) {
            String[] theListList = theList.split(":");
            String me = (new ComponentName(context, NotificationListener.class)).flattenToString();
            for (String next : theListList) {
                if (me.equals(next)) return true;
            }
        }
        return false;
    }

    public AutoTaskExpandableListAdpter(Context context, HashMap<String, List<AutoTaskItem>> expandableListItems, List<AutoTaskHeader> expandableListTitles, ExpandableListView expandableListView, String appPkgName, Activity thisActivity) {

        this.context = context;
        this.expandableListItems = expandableListItems;
        this.expandableListTitles = expandableListTitles;
        this.expandableListView = expandableListView;
        this.appPkgName=appPkgName;
        this.thisActivity=thisActivity;


    }



    private void saveSettingsToDataBase(String itemName,int value){
        DataBase.FeedReaderDb dbHelper= new DataBase.FeedReaderDb(context);
        SQLiteDatabase database= dbHelper.getReadableDatabase();
        String campo=null;
        String dynamicWhere=null;

        //Obtendo o ID da AutoTaskSetting para atualizar uma configuração especifica
        if(appPkgName!=null){
            Cursor c=database.rawQuery("SELECT "+ DataBase.FeedEntry.COLUMN_NAME_FKSETTING+" FROM "
                    + DataBase.FeedEntry.TABLE_APPLICATIONS_NAME+" WHERE "+ DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME+"= ?",new String[]{appPkgName});
            c.moveToFirst();
            dynamicWhere= DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_ID+"="+c.getInt(0);
            c.close();
        }
        else{
            dynamicWhere=DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING+"=1";
        }


        switch (itemName){
            case "Ultra Boost":
                break;
            case "Enable Wi-Fi":
                campo= DataBase.FeedEntry.COLUMN_NAME_ENABLE_WIFI;
                break;

            case "Mobile Data":
                campo= DataBase.FeedEntry.COLUMN_NAME_DATA;

                break;

            case "Bluetooth":
                campo= DataBase.FeedEntry.COLUMN_NAME_BLUETOOTH;
                break;

            case "GPS Location":
                campo= DataBase.FeedEntry.COLUMN_NAME_GPS;
                break;

            case "Brightness":
                campo= DataBase.FeedEntry.COLUMN_NAME_BRIGHTNESS;
                break;

            case "Media volume":
                campo= DataBase.FeedEntry.COLUMN_NAME_MEDIA_VOLUME;
                break;

            case "Call volume":
                campo= DataBase.FeedEntry.COLUMN_NAME_CALL_VULUME;
                break;
            case "Ring volume":
                campo= DataBase.FeedEntry.COLUMN_NAME_RING_VULUME;
                break;

            case "Block calls":
                campo= DataBase.FeedEntry.COLUMN_NAME_BLOCK_CALL;
                break;

            case "Block notifications":
                campo= DataBase.FeedEntry.COLUMN_NAME_BLOCK_NOTIFICATION;
                break;


        }
        database=dbHelper.getWritableDatabase();
        database.execSQL("UPDATE "+ DataBase.FeedEntry.TABLE_SETTINGS_NAME+" SET "+campo+"="+value+" WHERE "+
                dynamicWhere);
        database.close();

    }

    public void updateItems(AutoTaskSetting autoTaskSetting){

        expandableListItems.clear();
        AutoTaskListDataPump dataPump=new AutoTaskListDataPump(autoTaskSetting);
        expandableListItems=dataPump.getData();

    }

    public void lockSettingsPage(boolean lock){
        isLocked=lock;


        if(!lock){
            for(int i=0;i<expandableListTitles.size();i++){
                if(expandableListView.isGroupExpanded(i)) {
                    expandableListView.collapseGroup(i);
                    groupsToClose.put(i, i);
                }
            }
        }
        else{

            for(int i=0;i<expandableListTitles.size();i++) {

                if(groupsToClose.containsKey(i)){
                    expandableListView.expandGroup(groupsToClose.get(i));
                }
            }
        }


        expandableListView.refreshDrawableState();
        //expandableListView.invalidateViews();
        notifyDataSetChanged();
        expandableListView.invalidate();

    }

    private void showPremiumDialog(){

        new FancyGifDialog.Builder(context)
                .setTitle("Premium Feature")
                .setPositiveBtnText("Purchase")
                .setNegativeBtnText("Cancel")
                .setNegativeBtnBackground("#C0C0C0")
                .setMessage("Looks like you discovered a premium feature!" +
                        " Buy premium on Google play to use it.")
                .setTitleColor(context.getColor(R.color.darkActionBarColor))
                .setTitleSize(22f)
                .setTitleFontStyle(Typeface.BOLD)
                .setPositiveBtnBackground("#A227FF")
                .setGifResource(R.drawable.premium_background_header)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {//Watch Ads
                        AppController app=new AppController(context,thisActivity);
                        app.showPurchaseDialog();
                    }
                }).build();
    }




    @Override
    public int getGroupCount() {
        return expandableListTitles.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return expandableListItems.get(expandableListTitles.get(i).getTitle()).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.expandableListTitles.get(i).getTitle();
    }

    @Override
    public Object getChild(int listPos, int expandableListPos) {
        return this.expandableListItems.get(expandableListTitles.get(listPos).getTitle())
                .get(expandableListPos);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, final ViewGroup viewGroup) {
        LayoutInflater layoutInflater=(LayoutInflater)
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final String title=(String) getGroup(i);

        assert layoutInflater != null;
        view=layoutInflater.inflate(R.layout.auto_task_config_header,null);
        final ImageView dropDownArrow=view.findViewById(R.id.dropDownArrow);
        final LinearLayout autoTaskHeaderBackground=view.findViewById(R.id.autoTaskHeaderBackground);
        final TextView txtTitle=view.findViewById(R.id.autoTaskTitleHeader);

        autoTaskHeaderBackground.setEnabled(isLocked);
        dropDownArrow.setEnabled(isLocked);
        txtTitle.setEnabled(isLocked);

        //STYLING DISABLED EFFECT
        if(!isLocked){
            if(!expandableListView.isGroupExpanded(i) && groupsToClose.containsKey(i)){

                Animation animation=AnimationUtils.loadAnimation(context,R.anim.rotate_back);
                animation.setFillAfter(true);
                dropDownArrow.startAnimation(animation);
            }

            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int i) {
                    if(!groupsToClose.isEmpty()){
                        groupsToClose.remove(i);
                    }
                }
            });
            dropDownArrow.setRotation(0.0f);
            txtTitle.setAlpha(0.7f);
            autoTaskHeaderBackground.setAlpha(0.7f);
            dropDownArrow.setAlpha(0.7f);
            autoTaskHeaderBackground.setClickable(true);


        }


        autoTaskHeaderBackground.setBackgroundResource(expandableListTitles.get(i).getColor());
        ImageView autoTaskIconHeader=view.findViewById(R.id.autoTaskIconHeader);
        autoTaskIconHeader.setImageResource(expandableListTitles.get(i).getImg());
        txtTitle.setText(title);

        //autoTaskHeaderBackground.setEnabled(false);
        if(angles.containsKey(i)){
            dropDownArrow.setRotation(angles.get(i));

        }
        else{
            dropDownArrow.setRotation(0);
        }
        autoTaskHeaderBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation.AnimationListener animationListener;


                if(expandableListView.isGroupExpanded(i)){
                    angles.remove(i);
                    animationListener=new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            expandableListView.collapseGroup(i);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    };

                    Animation anim=AnimationUtils.loadAnimation(context,R.anim.rotate_back);
                    anim.setAnimationListener(animationListener);
                    dropDownArrow.startAnimation(anim);


                }
                else{
                    if(!angles.containsKey(i)){angles.put(i,90.00f);}
                    animationListener=new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            expandableListView.expandGroup(i);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    };
                    Animation animation=AnimationUtils.loadAnimation(context,R.anim.rotate);
                    animation.setAnimationListener(animationListener);
                    animation.setFillAfter(true);
                    dropDownArrow.startAnimation(animation);


                }



            }
        });





        return view;
    }

    @Override
    public View getChildView(final int i2, final int expandedListPos, boolean b, View view, ViewGroup viewGroup) {
        AutoTaskItem item=(AutoTaskItem) getChild(i2,expandedListPos);
        final String itemText=item.getItemName();



        LayoutInflater layoutInflater=(LayoutInflater)
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /*Most of the logic below is based on three numbers (-1,0,1), that has the following meaning
        -1=DISABLED
        0=NO ACTION
        1=ENABLED
         */
        switch (expandableListTitles.get(i2).getTitle()){

            case "Optimizations":
                assert layoutInflater != null;
                final String SEEK_BAR="boostSeekBar"+appPkgName,SWITCH_BUTTON_BOOST="boost"+appPkgName;
                view=layoutInflater.inflate(R.layout.auto_task_volume_item,null);
                final TextView txtTitleOpti=view.findViewById(R.id.txtTitleItemVolume);
                final ImageView imgOpti=view.findViewById(R.id.imgAutoTaskVolume);
                final Switch swtOpti=view.findViewById(R.id.swtEnableConfig);
                final IndicatorSeekBar discreteSeek=view.findViewById(R.id.indicatorSeekBar);

                final AppController appController=new AppController(context,thisActivity);

                txtTitleOpti.setText(itemText);
                imgOpti.setImageResource(item.getItemImg());
                sharedPreferences=context.getSharedPreferences("autoTaskSettings",Context.MODE_PRIVATE);
                //Loading config SWITCH_BUTTON_BOOST
                if(sharedPreferences.contains(SWITCH_BUTTON_BOOST)){
                    if(sharedPreferences.getBoolean(SWITCH_BUTTON_BOOST,false)){
                        swtOpti.setChecked(sharedPreferences.getBoolean(SWITCH_BUTTON_BOOST,false));
                        discreteSeek.setVisibility(View.VISIBLE);
                    }
                    else{
                        discreteSeek.setVisibility(View.GONE);
                    }

                }

                //Loading config SEEK_BAR
                if(sharedPreferences.contains(SEEK_BAR)){
                    discreteSeek.setProgress(sharedPreferences.getInt(SEEK_BAR,0));
                }

                swtOpti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        if(b){
                            discreteSeek.setVisibility(View.VISIBLE);

                            editor=sharedPreferences.edit();
                            editor.putBoolean(SWITCH_BUTTON_BOOST,true);
                            editor.apply();
                        }
                        else{
                            discreteSeek.setVisibility(View.GONE);
                            editor=sharedPreferences.edit();
                            editor.putBoolean(SWITCH_BUTTON_BOOST,false);
                            editor.apply();

                        }

                    }
                });

                discreteSeek.setOnSeekChangeListener(new OnSeekChangeListener() {
                    @Override
                    public void onSeeking(SeekParams seekParams) {


                    }

                    @Override
                    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                        if(appController.isUserPremium()) {
                            editor = sharedPreferences.edit();
                            editor.putInt(SEEK_BAR, seekBar.getProgress());
                            editor.apply();
                        }
                        else{
                            if (seekBar.getProgress() < 0) {
                                editor = sharedPreferences.edit();
                                editor.putInt(SEEK_BAR, 0);
                                editor.apply();
                            }
                            else{
                                discreteSeek.setProgress(0);
                                showPremiumDialog();
                            }
                        }
                    }
                });



                break;
            case "Wi-Fi and Networks":
                //Spinner Items
                ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(context,R.array.auto_task_spinner_items,R.layout.spinner_item);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                assert layoutInflater != null;
                view=layoutInflater.inflate(R.layout.auto_task_config_item,null);
                final ImageView itemImg=view.findViewById(R.id.imgAutoTaskConfig);
                final TextView itemTxt= view.findViewById(R.id.txtTitleItemAutoTask);
                final Spinner spiOpt= view.findViewById(R.id.spiOpt);


                itemTxt.setText(itemText);
                itemImg.setImageResource(item.getItemImg());
                spiOpt.setAdapter(adapter);


                //Setting load
                if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting()==1){
                    spiOpt.setSelection(1);


                }
                else if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting()==0) {
                    spiOpt.setSelection(0);

                }
                else if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting()==-1){
                    spiOpt.setSelection(2);

                }



                //Spinner Listener
                spiOpt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        //Caso o item seja Mobile Data é necessario requisitar a permissao MODIFY_PHONE_STATE
                        if (spiOpt.getSelectedItemPosition() == 0) {
                            expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(0);
                            saveSettingsToDataBase(expandableListItems.get
                                    (expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(), 0);
                        }
                        else if (spiOpt.getSelectedItemPosition() == 1) {
                            //Check bluetooth connection permission for android API 31 or superior
                            if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName().equals("Bluetooth")){
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        ActivityCompat.requestPermissions(thisActivity,new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                                70);
                                        return;
                                    }
                                }
                            }
                            expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(1);
                            saveSettingsToDataBase(expandableListItems.get
                                    (expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(), 1);
                        }
                        else {
                            expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(-1);
                            saveSettingsToDataBase(expandableListItems.get
                                    (expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(), -1);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });


                break;

            case "Volume and sounds":
                assert layoutInflater != null;
                view=layoutInflater.inflate(R.layout.auto_task_volume_item,null);
                final TextView txtTitle=view.findViewById(R.id.txtTitleItemVolume);
                final ImageView img=view.findViewById(R.id.imgAutoTaskVolume);
                final Switch swt=view.findViewById(R.id.swtEnableConfig);
                final SeekBar volumeControlBar=view.findViewById(R.id.volumeControlBar);

                txtTitle.setText(itemText);
                img.setImageResource(item.getItemImg());

                AudioManager audioManager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                //LOADING SWITCH STATE
                if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting()==-1){
                    swt.setChecked(false);
                    volumeControlBar.setVisibility(View.GONE);
                }
                else{
                    swt.setChecked(true);
                    volumeControlBar.setVisibility(View.VISIBLE);
                }

                //LOADING SEEKBAR STATE
                if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting()<0) {
                    volumeControlBar.setProgress(0);
                }
                else{
                    volumeControlBar.setProgress(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting());
                }

                //Setting max volume for each type
                if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName().equals("Media volume")){
                    assert audioManager != null;
                    volumeControlBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        volumeControlBar.setMin(audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC));
                    }
                }
                else if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName().equals("Call volume")){
                    assert audioManager != null;
                    volumeControlBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        volumeControlBar.setMin(audioManager.getStreamMinVolume(AudioManager.STREAM_VOICE_CALL));
                    }
                }
                else if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName().equals("Ring volume")){
                    assert audioManager != null;
                    volumeControlBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        volumeControlBar.setMin(audioManager.getStreamMinVolume(AudioManager.STREAM_RING));
                    }
                }


                //LISTENER
                //APPLY SETTING
                swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(volumeControlBar.getVisibility()==View.GONE) {
                            volumeControlBar.setVisibility(View.VISIBLE);

                            //expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(0);
                            saveSettingsToDataBase(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName()
                                    ,expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting());

                        }
                        else{
                            volumeControlBar.setVisibility(View.GONE);
                            // expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(-1);
                            //volumeControlBar.setProgress(0);
                            saveSettingsToDataBase(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(),-1);

                        }
                    }
                });

                volumeControlBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(seekBar.getProgress());

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        saveSettingsToDataBase(expandableListItems.get
                                (expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(),seekBar.getProgress());

                    }
                });
                break;

            case "Display":
                assert layoutInflater != null;
                view=layoutInflater.inflate(R.layout.auto_task_volume_item,null);
                final TextView titleDisplay=view.findViewById(R.id.txtTitleItemVolume);
                final ImageView imgDisplay=view.findViewById(R.id.imgAutoTaskVolume);
                final Switch swtDisplay=view.findViewById(R.id.swtEnableConfig);
                final SeekBar seekBarDisplay=view.findViewById(R.id.volumeControlBar);

                titleDisplay.setText(itemText);
                imgDisplay.setImageResource(item.getItemImg());
                Drawable drawable=context.getDrawable(R.drawable.purple_thumb);
                seekBarDisplay.setThumb(drawable);
                drawable=null;
                seekBarDisplay.setMax(255);

                //Caso o valor seja -1 o switch deve permanecer desmarcado
                //Setting load
                if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting()==-1){
                    swtDisplay.setChecked(false);
                    seekBarDisplay.setVisibility(View.GONE);
                }
                else{
                    swtDisplay.setChecked(true);
                    seekBarDisplay.setVisibility(View.VISIBLE);
                }


                if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting()<0) {
                    seekBarDisplay.setProgress(0);
                }
                else{
                    seekBarDisplay.setProgress(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting());
                }

                swtDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        //Checking WRITE_SETTINGS permission
                        if(Settings.System.canWrite(context)) {

                            if (seekBarDisplay.getVisibility() == View.GONE) {
                                seekBarDisplay.setVisibility(View.VISIBLE);
                                //expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(0);
                                saveSettingsToDataBase(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName()
                                        ,expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting());

                            } else {
                                seekBarDisplay.setVisibility(View.GONE);
                                //expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(-1);
                                saveSettingsToDataBase(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(),-1);

                            }
                        }
                        else{
                            swtDisplay.setChecked(false);
                            final AlertDialog.Builder alert=new AlertDialog.Builder(context);


                            alert.setMessage(R.string.modify_system_settings_explain)
                                    .setTitle(R.string.modify_system_settings_title).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                    context.startActivity(intent);
                                }
                            }).setNegativeButton("not now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {



                                }
                            });
                            alert.create();
                            alert.show();


                        }
                    }
                });

                seekBarDisplay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(seekBarDisplay.getProgress());

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        saveSettingsToDataBase(expandableListItems.get
                                (expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(),seekBar.getProgress());

                    }
                });
                break;

            case "Calls and Notifications":
                assert layoutInflater != null;
                view=layoutInflater.inflate(R.layout.auto_task_volume_item,null);
                final TextView titleCalls=view.findViewById(R.id.txtTitleItemVolume);
                final ImageView imgCalls=view.findViewById(R.id.imgAutoTaskVolume);
                final Switch swtCalls=view.findViewById(R.id.swtEnableConfig);

                titleCalls.setText(itemText);
                imgCalls.setImageResource(item.getItemImg());
                //LOAD STATE
                if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemSetting()==-1){
                    swtCalls.setChecked(false);
                }
                else{
                    swtCalls.setChecked(true);
                }

                //BLOCK NOTIFICATIONS PERMISSION CHECK
                swtCalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        //For NOTIFICATION_BLOCK Item
                        if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName().equals("Block notifications") &&
                                VerifyNotificationPermission(context)) {
                            if (!b) {
                                expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(-1);
                                saveSettingsToDataBase(expandableListItems.get
                                        (expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(), -1);

                            }
                            else {
                                expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(1);
                                saveSettingsToDataBase(expandableListItems.get
                                        (expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(), 1);
                            }
                        }

                        else if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName().equals("Block notifications") &&
                                !VerifyNotificationPermission(context)){
                            final AlertDialog.Builder alert=new AlertDialog.Builder(context);



                            alert.setMessage(R.string.notification_permission_explain)
                                    .setTitle(R.string.notification_permission_title)
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                                            context.startActivity(intent);
                                        }
                                    }).setNegativeButton("not now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            alert.create();
                            alert.show();
                            swtCalls.setChecked(false);
                        }

                        //CALL BLOCK Item
                        if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName()
                                .equals("Block calls") && ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                                == PackageManager.PERMISSION_GRANTED){
                            if (!b) {
                                expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(-1);
                                saveSettingsToDataBase(expandableListItems.get
                                        (expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(), -1);

                            }
                            else {
                                expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).setItemSetting(1);
                                saveSettingsToDataBase(expandableListItems.get
                                        (expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName(), 1);
                            }
                        }
                        else if(expandableListItems.get(expandableListTitles.get(i2).getTitle()).get(expandedListPos).getItemName()
                                .equals("Block calls") && ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                                == PackageManager.PERMISSION_DENIED){

                            ActivityCompat.requestPermissions(thisActivity,new String[]{Manifest.permission.CALL_PHONE
                                    ,Manifest.permission.READ_PHONE_STATE},CALL_PHONE_CODE);
                            swtCalls.setChecked(false);



                        }

                    }
                });
                break;

            default:
                assert layoutInflater != null;
                view=layoutInflater.inflate(R.layout.auto_task_volume_item,null);
                final TextView title=view.findViewById(R.id.txtTitleItemVolume);
                final ImageView imgV=view.findViewById(R.id.imgAutoTaskVolume);
                final Switch swtT=view.findViewById(R.id.swtEnableConfig);

                title.setText(itemText);
                imgV.setImageResource(item.getItemImg());

                break;



        }


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }




}

