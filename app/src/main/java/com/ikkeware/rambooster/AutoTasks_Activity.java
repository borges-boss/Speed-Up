package com.ikkeware.rambooster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import android.app.ActivityOptions;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.ikkeware.rambooster.model.DataBase;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;

import CustomComponents.FancyGifDialog;
import CustomComponents.FancyGifDialogListener;

import static com.ikkeware.rambooster.model.AppTheme.getCurrentApplicationTheme;
import static com.ikkeware.rambooster.utils.Utils.shortenText;


public class AutoTasks_Activity extends AppCompatActivity {
    SQLiteDatabase database;
    DataBase.FeedReaderDb dbHelper;
    String packageName;
    Context context=this;
    private AutoTaskListDataPump dataPump;
    private ExpandableListView expandableListView;
    private  AutoTaskExpandableListAdpter adpter;
    private RewardedAd mRewardedAd;
    private AppController appController;
    AutoTaskSetting thisAutoTaskSetting;
    private boolean userGotTheReward = false;
    Switch swtTool;

    private final String UID="ca-app-pub-3549767228651381/7362571152";


    public void setAnimation(){

        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.START);
        slide.setDuration(200);
        slide.setInterpolator(new LinearInterpolator());

        getWindow().setEnterTransition(slide);
    }

    private void initAdMob(Context context){
        if(!appController.isUserPremium()) {
            MobileAds.initialize(context);
            AdView adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    private void setLayoutTheme(Toolbar toolbar){


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(AutoTasks_Activity.this).toBundle());
            }
        });
        toolbar.setTitleMarginStart(0);
        toolbar.setTitle("Auto task");

        if(getCurrentApplicationTheme()==R.style.DarkAppTheme){
            toolbar.setNavigationIcon(R.drawable.back_arrow_white);

        }
        else{
            toolbar.setNavigationIcon(R.drawable.back_arrow);

        }

    }
    public AutoTaskSetting getApplicationSettings(){
        dbHelper= new DataBase.FeedReaderDb(context);
        database= dbHelper.getReadableDatabase();
        Cursor c=null;
        AutoTaskSetting autoTaskSetting = null;

        try{
            /*Se o package name passado via intent for nulo, busque pela configuração global.
             * Esta logica é necessario para abrir as configurações globais pelo icone do menu superior */

            if(getIntent().getStringExtra("pkgNameForSettings")!=null) {
                c = database.rawQuery("SELECT * FROM "+ DataBase.FeedEntry.TABLE_APPLICATIONS_NAME+
                        " INNER JOIN auto_task_settings ON applications.FK_settingsId=auto_task_id WHERE " +
                        DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME+ "= ? AND "
                        + DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING+" <> 1", new String[]{getIntent().getStringExtra("pkgNameForSettings")});

                if(c.getCount()>0) {
                    c.moveToFirst();
                    autoTaskSetting = new AutoTaskSetting(c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_ID)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_ENABLE_WIFI)), c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_DATA)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BLUETOOTH)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_GPS)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BRIGHTNESS)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_MEDIA_VOLUME)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_CALL_VULUME)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_RING_VULUME)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BLOCK_CALL)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BLOCK_NOTIFICATION)),
                            c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING)));
                    c.close();
                }


            }
            else{
                c=database.rawQuery("select * from "+ DataBase.FeedEntry.TABLE_SETTINGS_NAME+" where is_global_setting=1;",null);
                c.moveToFirst();
                autoTaskSetting= new AutoTaskSetting(c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_ID)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_ENABLE_WIFI)),c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_DATA)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BLUETOOTH)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_GPS)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BRIGHTNESS)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_MEDIA_VOLUME)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_CALL_VULUME)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_RING_VULUME)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BLOCK_CALL)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BLOCK_NOTIFICATION)),
                        c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING)));
                c.close();
            }
            //If the last query didn't return anything return fixed values

        }catch(SQLException ex){
            System.err.println("SQL error: "+ex.getMessage());
        }
        database.close();
        return autoTaskSetting;
    }

    private void updateSettingsInDataBase(int setting_value){
        DataBase.FeedReaderDb dbHelper= new DataBase.FeedReaderDb(context);
        SQLiteDatabase database=dbHelper.getWritableDatabase();

        //Validando se a aplicação ja possui uma configuraçao especifica
        //Se o getIs_global_setting for igual a 1 significa que a aplicação ainda não possui uma configuração individual


        if(thisAutoTaskSetting==null){
            database=dbHelper.getWritableDatabase();
            //Validando se a aplicação ja possui uma configuraçao especifica

            //1: INSERINDO NOVA CONFIGUTAÇÂO INDIVIAUDAL NO BD
            String INSERT_NEW_SETTINGS="INSERT INTO " + DataBase.FeedEntry.TABLE_SETTINGS_NAME + " ("
                    + DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE + ","
                    + DataBase.FeedEntry.COLUMN_NAME_ENABLE_WIFI + ","
                    + DataBase.FeedEntry.COLUMN_NAME_DATA + ","
                    + DataBase.FeedEntry.COLUMN_NAME_BLUETOOTH + ","
                    + DataBase.FeedEntry.COLUMN_NAME_GPS + ","
                    + DataBase.FeedEntry.COLUMN_NAME_BRIGHTNESS + ","
                    + DataBase.FeedEntry.COLUMN_NAME_MEDIA_VOLUME + ","
                    + DataBase.FeedEntry.COLUMN_NAME_CALL_VULUME + ","
                    + DataBase.FeedEntry.COLUMN_NAME_RING_VULUME + ","
                    + DataBase.FeedEntry.COLUMN_NAME_BLOCK_CALL + ","
                    + DataBase.FeedEntry.COLUMN_NAME_BLOCK_NOTIFICATION + "," +
                    DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING + ") VALUES (1,0,0,0,0,-1,-1,-1,-1,-1,-1,-1);";
            database.execSQL(INSERT_NEW_SETTINGS);

            //2: ATUALIZANDO CHAVE ESTRANGEIRA DA TABELA applications COM O ID DA NOVA AUTO TASK SETTING
            String UPDATE_APPLICATION_FK="UPDATE " + DataBase.FeedEntry.TABLE_APPLICATIONS_NAME + " SET " +
                    DataBase.FeedEntry.COLUMN_NAME_FKSETTING + "= (SELECT rowid FROM auto_task_settings ORDER BY rowid DESC limit 1)"
                    +  " WHERE package_name= '"+getIntent().getStringExtra("pkgNameForSettings")+"' ";

            database.execSQL(UPDATE_APPLICATION_FK);
            database=dbHelper.getReadableDatabase();

            //3: OBETENDO REGISTRO DA TABELA application QUE ACABOU DE SER INSERIDO NO DB
            Cursor c=database.rawQuery("SELECT * FROM " + DataBase.FeedEntry.TABLE_APPLICATIONS_NAME + " INNER JOIN auto_task_settings ON applications.FK_settingsId=auto_task_id WHERE package_name= ?"
                    ,new String[]{getIntent().getStringExtra("pkgNameForSettings")});

            //4: ATUALIZANDO OBJETO thisAutoTaskSetting COM O REGISTRO QUE ACABOU DE SER INSERIDO
            c.moveToFirst();
            thisAutoTaskSetting= new AutoTaskSetting(c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_ID)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_ENABLE_WIFI)),c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_DATA)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BLUETOOTH)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_GPS)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BRIGHTNESS)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_MEDIA_VOLUME)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_CALL_VULUME)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_RING_VULUME)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BLOCK_CALL)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_BLOCK_NOTIFICATION)),
                    c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING)));

            c.close();
            database.close();

        }
        else{
            database.execSQL("UPDATE " + DataBase.FeedEntry.TABLE_SETTINGS_NAME + " SET is_setting_active="+setting_value
                    +" WHERE auto_task_id="+thisAutoTaskSetting.getAutoTaskId());


            database.close();
        }

    }

    private boolean showShortcutTutorialDialogIfIsFirstTime(){
        UserConfigs configs=new UserConfigs(context);
        SharedPreferences sharedPreferences=context.getSharedPreferences(configs.USER_CONFIG+"."+configs.TUTORIAL_DIALOG_CONFIG,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        if(sharedPreferences.getString(configs.IS_FIRST_TIME,"Y").equals("Y")) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("About the shortcuts")
                    .setMessage(R.string.text_main_tutorial)
                    .setPositiveButton("Alright, I got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setCancelable(true);

            dialog.create();
            dialog.show();
            editor.putString(configs.IS_FIRST_TIME,"N");
            editor.apply();
            return true;

        }
        return  false;
    }

    private void showAndroidSevenTip(){

        UserConfigs configs=new UserConfigs(context);
        SharedPreferences sharedPreferences=context.getSharedPreferences(configs.USER_CONFIG+"."+configs.TUTORIAL_DIALOG_CONFIG,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        if(sharedPreferences.getString(configs.IS_FIRST_TIME_TIP_7,"Y").equals("Y")) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("For android 7.1")
                    .setMessage(R.string.text_page7_1)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setCancelable(true);

            dialog.create();
            dialog.show();
            editor.putString(configs.IS_FIRST_TIME_TIP_7,"N");
            editor.apply();
        }

    }

    private void showInfoPremiumDialog(){

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

            if (!appController.isUserPremium()) {
                new FancyGifDialog.Builder(this)
                        .setTitle("Premium Feature")
                        .setTitleSize(22f)
                        .setPositiveBtnText("Watch Ads")
                        .setNegativeBtnText("Purchase")
                        .setMessage("Looks like you discovered a premium feature!" +
                                " Buy premium or watch an ad to use it.")
                        .setTitleColor(getColor(R.color.darkActionBarColor))
                        .setPositiveBtnBackground("#A227FF")
                        .setNegativeBtnBackground("#A227FF")
                        .setGifResource(R.drawable.premium_background_header)   //Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {//Watch Ads
                                if(mRewardedAd!=null) {
                                    mRewardedAd.show(getParent(), rewardItem -> userGotTheReward = true);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Ad Failed to load :(",Toast.LENGTH_LONG).show();
                                }

                            }
                        }).OnNegativeClicked(new FancyGifDialogListener() {//Purchase Premium
                    @Override
                    public void OnClick() {
                        AppController app = new AppController(getApplicationContext(), AutoTasks_Activity.this);
                        app.showPurchaseDialog();
                    }
                }).build();

            }
            else {
                createShortcut();
                if(android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                    showAndroidSevenTip();
                }

            }
        }
        else{
            new FancyGifDialog.Builder(this).setGifResource(R.drawable.warning_header)
                    .setTitle("Android version not compatible")
                    .setTitleFontStyle(Typeface.BOLD)
                    .setTitleColor((getCurrentApplicationTheme()==R.style.DarkAppTheme)?Color.WHITE:Color.BLACK)
                    .setMessage("This feature is only available for Android 7.1 and higher.")
                    .setPositiveBtnBackground("#A227FF")
                    .setPositiveBtnText("Okay :(")
                    .isCancellable(true)
                    .build();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(getCurrentApplicationTheme());

        setAnimation();
        setContentView(R.layout.activity_auto_tasks_);
        appController=new AppController(getApplicationContext(),this);
        //ADS INITIALIZATION
        initAdMob(this);
        final Toolbar toolbar = findViewById(R.id.toolbar_auto_tasks);
        setSupportActionBar(toolbar);
        expandableListView = findViewById(R.id.expandableListView);
        setLayoutTheme(toolbar);

        createRewardedAd();

        toolbar.setTitle((getIntent().getStringExtra("appName")!=null)
                ?shortenText(getIntent().getStringExtra("appName"),28):"Global Settings");
        thisAutoTaskSetting=getApplicationSettings();
        dataPump=new AutoTaskListDataPump(thisAutoTaskSetting);
        adpter=new AutoTaskExpandableListAdpter(context,dataPump.getData(),dataPump.getTitles(), expandableListView,getIntent().getStringExtra("pkgNameForSettings"),AutoTasks_Activity.this);
        expandableListView.setAdapter(adpter);
        expandableListView.setVerticalScrollBarEnabled(true);
        expandableListView.setSmoothScrollbarEnabled(true);

        packageName=getIntent().getStringExtra("pkgNameForSettings");
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                dbHelper= new DataBase.FeedReaderDb(context);
                database=dbHelper.getReadableDatabase();
                adpter.updateItems(getApplicationSettings());

            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                //   adpter.updateItems(getApplicationSettings());
            }
        });
        expandableListView.setDividerHeight(7);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auto_task, menu);
        MenuItem menuItem=menu.findItem(R.id.swtTool);
        final MenuItem shortcut=menu.findItem(R.id.shortCut);
        menuItem.setActionView(R.layout.switch_toolbar_layout);
        swtTool=menuItem.getActionView().findViewById(R.id.swToolBar);

        if(getCurrentApplicationTheme()==R.style.DarkAppTheme){

            int[][] states = new int[][] {
                    new int[] {-android.R.attr.state_checked},
                    new int[] {android.R.attr.state_checked},
            };

            int[] thumbColors = new int[] {
                    Color.parseColor("#FBFBFB"),
                    Color.parseColor("#FBFBFB"),
            };

            int[] trackColors = new int[] {
                    Color.parseColor("#D4A7F6"),
                    Color.parseColor("#D4A7F6"),
            };


            DrawableCompat.setTintList(DrawableCompat.wrap(swtTool.getThumbDrawable()), new ColorStateList(states, thumbColors));
            DrawableCompat.setTintList(DrawableCompat.wrap(swtTool.getTrackDrawable()), new ColorStateList(states, trackColors));
        }

        //INSERT NEW SETTINGS IF DOESN'T
        //Inicializando Switch button


        swtTool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (adpter != null) {

                    //Atualizar configurações
                    if (isChecked) {
                        adpter.lockSettingsPage(true);
                        shortcut.setEnabled(true);

                        updateSettingsInDataBase(1);
                    } else {
                        adpter.lockSettingsPage(false);
                        shortcut.setEnabled(false);

                        updateSettingsInDataBase(-1);
                    }

                }

            }
        });

        //Initializing button
        AutoTaskSetting temp=getApplicationSettings();
        if(temp==null && getIntent().getStringExtra("pkgNameForSettings")!=null){
            swtTool.setChecked(false);
            adpter.lockSettingsPage(false);

        }
        else if(temp!=null && getIntent().getStringExtra("pkgNameForSettings")==null){
            swtTool.setEnabled(false);
            adpter.lockSettingsPage(true);
            shortcut.setVisible(false);
        }
        else if(temp!=null && getIntent().getStringExtra("pkgNameForSettings")!=null){
            //Testa se a configuração individual esta ativa ou não
            if(temp.getIs_active()==1){
                swtTool.setChecked(true);
                adpter.lockSettingsPage(true);
                shortcut.setEnabled(true);
            }
            else{
                swtTool.setChecked(false);
                adpter.lockSettingsPage(false);
                shortcut.setEnabled(false);
            }
        }



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.shortCut) {

            UserConfigs configs=new UserConfigs(context);
            SharedPreferences sharedPreferences=context.getSharedPreferences(configs.USER_CONFIG+"."+configs.TUTORIAL_DIALOG_CONFIG,MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();

            if(sharedPreferences.getString(configs.IS_FIRST_TIME,"Y").equals("Y")) {

                new FancyGifDialog.Builder(context)
                        .setTitle("About this feature")
                        .setTitleColor((getCurrentApplicationTheme()==R.style.DarkAppTheme)?Color.WHITE:Color.BLACK)
                        .setTitleFontStyle(Typeface.BOLD)
                        .setMessage(getString(R.string.text_main_tutorial))
                        .setPositiveBtnText("Got it")
                        .setPositiveBtnBackground("#A227FF")
                        .setGifResource(R.drawable.shortcut_tutorial_header)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                showInfoPremiumDialog();
                            }
                        }).build();

                editor.putString(configs.IS_FIRST_TIME,"N");
                editor.apply();
            }
            else{
                showInfoPremiumDialog();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private void createRewardedAd(){

        AdRequest adRequest = new AdRequest.Builder().build();
        //ca-app-pub-3549767228651381/3541496203
        //ca-app-pub-3940256099942544/5224354917
        RewardedAd.load(this, "ca-app-pub-3549767228651381/3541496203",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                Toast.makeText(getApplicationContext(),"Ad Failed to load",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                if(userGotTheReward){
                                    createShortcut();
                                    userGotTheReward = false;
                                }
                            }
                        });
                        Log.d("AutoTaskActivity", "Ad was loaded.");
                    }
                });
    }

    private void createShortcut(){
        Intent intent=new Intent(this,ShortcutController.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("packageNameForLaunch",packageName);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager=getSystemService(ShortcutManager.class);

            ShortcutInfo shortcut= setShortcuts(shortcutManager,intent,packageName);

            //If version superior to 8.0 Oreo, than create the pinned shortcut auto
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
                    && shortcutManager.isRequestPinShortcutSupported()) {

                Intent  pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(shortcut);

                PendingIntent successCallback = PendingIntent.getBroadcast(context, /* request code */ 0,
                        pinnedShortcutCallbackIntent, /* flags */ 0);

                shortcutManager.requestPinShortcut(shortcut,
                        successCallback.getIntentSender());
            }
            else {
                assert shortcutManager != null;

                if (shortcutManager.getDynamicShortcuts().size() == 0) {
                    shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
                }
                else if (shortcutManager.getDynamicShortcuts().size() == shortcutManager.getMaxShortcutCountPerActivity()) {
                    List<ShortcutInfo> list = shortcutManager.getDynamicShortcuts();
                    list.remove(shortcutManager.getDynamicShortcuts().size() - 1);

                    shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
                    for (int i = 0; i < list.size(); i++) {
                        shortcutManager.addDynamicShortcuts(Arrays.asList(setShortcuts
                                (shortcutManager, intent, list.get(i).getId())));
                    }

                }
                else {
                    shortcutManager.addDynamicShortcuts(Arrays.asList(shortcut));
                }
                Toast.makeText(getApplicationContext(),"Shortcut created!", Toast.LENGTH_LONG).show();
                //HERE

            }


        }

    }

    private ShortcutInfo setShortcuts(ShortcutManager shortcutManager, Intent intent,String id){
        ShortcutInfo shortcut= null;
        PackageManager packageManager=getPackageManager();
        //Getting bitmap from drawable
        Drawable drawable= null;
        try {
            drawable = packageManager.getApplicationIcon(id);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        bitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                shortcut = new ShortcutInfo.Builder(getApplicationContext(),id)
                        .setIcon(Icon.createWithBitmap(bitmap))
                        .setShortLabel(packageManager.getApplicationLabel(packageManager.getApplicationInfo(id,0)))
                        .setLongLabel(packageManager.getApplicationLabel(packageManager.getApplicationInfo(id,0)))
                        .setIntent(intent).build();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return shortcut;

    }




    AutoTaskSetting taskSetting;

    public class LoadSettings extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            taskSetting=getApplicationSettings();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.err.println("onPost");

                }
            });

            super.onPostExecute(aVoid);
        }
    }

}
