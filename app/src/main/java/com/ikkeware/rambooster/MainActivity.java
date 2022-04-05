package com.ikkeware.rambooster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.ikkeware.rambooster.adapter.MyGamesListAdpter;
import com.ikkeware.rambooster.model.AppDetail;
import com.ikkeware.rambooster.model.DataBase;
import com.ikkeware.rambooster.utils.DataEventUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.ikkeware.rambooster.model.AppTheme.getCurrentApplicationTheme;
import static com.ikkeware.rambooster.model.AppTheme.setApplicationTheme;
import static com.ikkeware.rambooster.utils.AnimationUtils.createIntroCircularTransitionAnimation;
import static com.ikkeware.rambooster.utils.DatabaseUtils.getIdByApplicationPackage;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DataEventUtils {


    public Menu themeIcon;
    public RecyclerView myRecyclerView;
    public MyGamesListAdpter adpter;
    private List<AppDetail> appDetailList = new ArrayList<>();
    private ImageView recyclerBack;
    DrawerLayout drawer;
    SimpleDateFormat dateFormat;
    private AppController appController;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    TextView txtMemoryCleaned;
    ProgressBar progressBarCleaner;
    //SharedPreferences sharedPreferences;
    private static int recentlyItemsCount;
    TextView[] recentlyTextViews;
    BuyPremiumDialog bottomSheet;
    TextView txtNavBuyPremium;
    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;
    // private final String RECENTLY_FILE_KEY="com.ikkeware.rambooster.RECENTLY";


    private void startSelectGamesActivity() {
        Intent intent = new Intent(getApplicationContext(), SelectGamesActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }




    /*Front_End Section*/

    private void alterTheme() {
        final String CARD_DARK_COLOR = "#3B3B3B", BACKGROUND_DARK = "#2D2C2C";
        final String CARD_LIGHT_COLOR = "#FFFFFF", BACKGROUND_LIGHT = "#F1F1F1";
        ConstraintLayout mainActivityBack = findViewById(R.id.mainActivityBack);
        CardView cardClearMemory = findViewById(R.id.cardClearMemory);
        CardView cardItems = findViewById(R.id.cardItems);
        TextView txtTitleAdded = findViewById(R.id.txtTitleAdded);
        TextView txtTitleClearCard = findViewById(R.id.txtTitleClearCard);
        TextView txtTitleRecently = findViewById(R.id.txtTitleRecently);
        if (txtNavBuyPremium == null) {
            txtNavBuyPremium = findViewById(R.id.nav_buy_premium);
        }
        NavigationView navView = findViewById(R.id.nav_view);
        ImageView recyclerViewBackground = findViewById(R.id.recyclerBackground);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);


        if (getCurrentApplicationTheme() == R.style.LightAppTheme) {//Changing to Dark mode
            setApplicationTheme(R.style.DarkAppTheme);
            themeIcon.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_moon));
            themeIcon.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_settings_white));
            getWindow().getDecorView().setSystemUiVisibility(0);
            getWindow().setStatusBarColor(Color.parseColor("#404040"));
            txtTitleAdded.setTextColor(Color.WHITE);
            txtTitleRecently.setTextColor(Color.WHITE);
            txtTitleClearCard.setTextColor(Color.WHITE);

            toolbar.setBackgroundColor(Color.parseColor("#3B3B3B"));
            toolbar.setTitleTextColor(Color.parseColor(CARD_LIGHT_COLOR));
            toggle.getDrawerArrowDrawable().setColor(Color.parseColor(CARD_LIGHT_COLOR));
            adpter.textColor = "#FFFFFF";
            adpter.notifyItemRangeChanged(0, adpter.getItemCount());
            mainActivityBack.setBackgroundColor(Color.parseColor(BACKGROUND_DARK));

            cardItems.setCardBackgroundColor(Color.parseColor(CARD_DARK_COLOR));
            cardClearMemory.setCardBackgroundColor(Color.parseColor(CARD_DARK_COLOR));

            navView.setBackgroundColor(Color.parseColor(BACKGROUND_DARK));
            navView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
            navView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
            txtNavBuyPremium.setTextColor(Color.WHITE);
            txtNavBuyPremium.setCompoundDrawableTintList(ColorStateList.valueOf(Color.WHITE));
            recentlyTextViews[0].setTextColor(Color.WHITE);
            recentlyTextViews[1].setTextColor(Color.WHITE);
            recentlyTextViews[2].setTextColor(Color.WHITE);
            recentlyTextViews[3].setTextColor(Color.WHITE);

            if (recyclerViewBackground.getVisibility() == View.VISIBLE) {
                recyclerViewBackground.setImageTintList(ColorStateList.valueOf(Color.parseColor("#343434")));
            }
        } else {

            setApplicationTheme(R.style.LightAppTheme);
            themeIcon.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_sun));
            themeIcon.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_settings));

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
            txtTitleAdded.setTextColor(Color.parseColor("#515151"));
            txtTitleRecently.setTextColor(Color.parseColor("#515151"));
            txtTitleClearCard.setTextColor(Color.parseColor(BACKGROUND_DARK));
            toolbar.setBackgroundColor(Color.parseColor(CARD_LIGHT_COLOR));
            toolbar.setTitleTextColor(Color.parseColor("#757575"));
            toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#9717F8"));
            adpter.textColor = BACKGROUND_DARK;
            adpter.notifyItemRangeChanged(0, adpter.getItemCount());
            mainActivityBack.setBackgroundColor(Color.parseColor(BACKGROUND_LIGHT));
            cardItems.setCardBackgroundColor(Color.parseColor(CARD_LIGHT_COLOR));
            cardClearMemory.setCardBackgroundColor(Color.parseColor(CARD_LIGHT_COLOR));
            navView.setBackgroundColor(Color.WHITE);
            navView.setItemIconTintList(ColorStateList.valueOf(Color.parseColor("#6A6767")));
            navView.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#6A6767")));
            txtNavBuyPremium.setTextColor(Color.parseColor("#9717F8"));
            txtNavBuyPremium.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#9717F8")));

            recentlyTextViews[0].setTextColor(Color.parseColor(BACKGROUND_DARK));
            recentlyTextViews[1].setTextColor(Color.parseColor(BACKGROUND_DARK));
            recentlyTextViews[2].setTextColor(Color.parseColor(BACKGROUND_DARK));
            recentlyTextViews[3].setTextColor(Color.parseColor(BACKGROUND_DARK));
            if (recyclerViewBackground.getVisibility() == View.VISIBLE) {
                recyclerViewBackground.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
            }
        }


        createIntroCircularTransitionAnimation(coordinatorLayout, 300);

    }


    public void createInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        //ca-app-pub-3549767228651381/6184285693
        //ca-app-pub-3940256099942544/1033173712
        InterstitialAd.load(this, "ca-app-pub-3549767228651381/6184285693", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                            }
                        });
                        Log.d("AD", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("AD", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    public void createRewardedAdClearRAM() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3549767228651381/6184285693",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("Ad", "Error");
                        Toast.makeText(getApplicationContext(), "Ad failed to load", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        Log.d("Ad", "Loaded");
                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.

                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Toast.makeText(getApplicationContext(), "Ad failed to show", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                mRewardedAd = null;
                            }
                        });

                    }
                });
    }


    public void initAds() {
        if (!appController.isUserPremium()) {
            MobileAds.initialize(this, initializationStatus -> {
                //createRewardedAdClearRAM();
            });
        }
    }


    public void setActivityTransitionAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Slide slide = new Slide();
            Slide slideIn = new Slide();

            slide.setSlideEdge(Gravity.END);
            slide.setDuration(200);
            slide.setInterpolator(new LinearInterpolator());

            slideIn.setSlideEdge(Gravity.END);
            slideIn.setDuration(200);
            slideIn.setInterpolator(new LinearInterpolator());

            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slideIn);
        }
    }

    public void animateTransition(Context context, View view, int animationResourceId, Animation.AnimationListener animationListener) {
        Animation animation = AnimationUtils.loadAnimation(context, animationResourceId);
        if (animationListener != null) {
            animation.setAnimationListener(animationListener);
        }
        animation.setFillAfter(true);

        view.startAnimation(animation);
    }

    public void animateTransition(Context context, View view, Animation animation, Animation.AnimationListener animationListener) {

        if (animationListener != null) {
            animation.setAnimationListener(animationListener);
        }
        animation.setFillAfter(true);

        view.startAnimation(animation);
    }



    /*Back_End Section*/

    private void initRatingDialog() {

        SharedPreferences.Editor editor;
        UserConfigs configs = new UserConfigs(MainActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences(configs.USER_CONFIG + "." + configs.RATTING_DIALOG_CONFIG, MODE_PRIVATE);
        //Primeira vez que o usuario entrou no app
        if (!sharedPreferences.contains(configs.AMOUNT_TIME_SHOWED)) {
            editor = sharedPreferences.edit();
            editor.putInt(configs.AMOUNT_TIME_SHOWED, 1);
            editor.apply();
        }

        if (!sharedPreferences.contains(configs.RATED_APP)) {
            editor = sharedPreferences.edit();
            editor.putBoolean(configs.RATED_APP, false);
            editor.apply();

        }

        int amountTime = sharedPreferences.getInt(configs.AMOUNT_TIME_SHOWED, 0);
        boolean ratedApp = sharedPreferences.getBoolean(configs.RATED_APP, false);
        //Exiba o dialog de avaliação de 5 em 5 vezes
        if (!ratedApp) {

            if (amountTime % 5 == 0) {
                configs.showRattingDialog();
                editor = sharedPreferences.edit();
                editor.putInt(configs.AMOUNT_TIME_SHOWED, sharedPreferences.getInt(configs.AMOUNT_TIME_SHOWED, 0) + 1);
                editor.apply();
            } else {
                editor = sharedPreferences.edit();
                editor.putInt(configs.AMOUNT_TIME_SHOWED, sharedPreferences.getInt(configs.AMOUNT_TIME_SHOWED, 0) + 1);
                editor.apply();
            }


        }
    }

    private void initGlobalSettingsInDataBase() {//Este metodo so sera executado se a quantidade de registros for menor que 0
        DataBase.FeedReaderDb dbHelper = new DataBase.FeedReaderDb(getApplicationContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + DataBase.FeedEntry.TABLE_SETTINGS_NAME, null);
        cursor.moveToFirst();
        int co = cursor.getInt(0);

        if (co <= 0) {
            database.execSQL(DataBase.FeedEntry.INSERT_GLOBAL_CONFIG);
            //database.rawQuery("SELECT "+DataBase.FeedEntry.COLUMN_NAME_BLOCK_CALL+" FROM "+ DataBase.FeedEntry.TABLE_SETTINGS_NAME,null);

        }

        cursor.close();
        database.close();
    }

    @SuppressLint("SimpleDateFormat")
    private void initRecentlyLauchedApps() {
        DataBase.FeedReaderDb dbHelper = new DataBase.FeedReaderDb(getApplicationContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + DataBase.FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME, null);
        cursor.moveToFirst();
        int co = cursor.getInt(0);

        if (co == 0) {
            //Getting global setting id
            cursor = database.rawQuery("SELECT " + DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_ID
                    + " FROM " + DataBase.FeedEntry.TABLE_SETTINGS_NAME + " WHERE "
                    + DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING + "=1", null);

            cursor.moveToFirst();
            //Inserting first application
            database = dbHelper.getWritableDatabase();
            database.execSQL("INSERT INTO " + DataBase.FeedEntry.TABLE_APPLICATIONS_NAME + "("
                    + DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME + ", " + DataBase.FeedEntry.COLUMN_NAME_FKSETTING + ", "
                    + DataBase.FeedEntry.COLUMN_NAME_ISACTIVE + ") VALUES ('" + getPackageName() + "', "
                    + cursor.getInt(cursor.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_ID)) + ", -1);");

            //Getting id of the first application
            database = dbHelper.getReadableDatabase();
            cursor = database.rawQuery("SELECT " + DataBase.FeedEntry.COLUMN_NAME_APPLICATION_ID + " FROM "
                    + DataBase.FeedEntry.TABLE_APPLICATIONS_NAME + " WHERE "
                    + DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME + "= '" + getPackageName() + "' ", null);
            cursor.moveToFirst();

            //Insert inicial Recently lauched app
            database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            final Date date = new Date();
            dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            values.put(DataBase.FeedEntry.COLUMN_NAME_DATETIMELAUNCHED, dateFormat.format(date));
            values.put(DataBase.FeedEntry.COLUMN_NAME_FKAPPLICATIONID, cursor.getInt(0));
            database.insert(DataBase.FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME, null, values);
            cursor.close();
            database.close();

        }

        //  public static final String INSERT_FIRST_RECENTLY_APP="INSERT INTO "+FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME+" ("
        //        +FeedEntry.COLUMN_NAME_DATETIMELAUNCHED+", "+FeedEntry.COLUMN_NAME_FKAPPLICATIONID;
    }

    private List<String> getRecentlyApps(Context context) {
        DataBase.FeedReaderDb dbHelper = new DataBase.FeedReaderDb(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        final Cursor cursor = database.rawQuery("SELECT DISTINCT " + DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME + ", "
                + DataBase.FeedEntry.COLUMN_NAME_FKAPPLICATIONID + " FROM "
                + DataBase.FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME
                + " INNER JOIN " + DataBase.FeedEntry.TABLE_APPLICATIONS_NAME
                + " ON recently_applications.FK_application_id=application_id ORDER BY "
                + DataBase.FeedEntry.COLUMN_NAME_DATETIMELAUNCHED + " DESC LIMIT 4;", null);

        while (cursor.moveToNext()) {
            //Testar se o Package existe no dispositivo do usuario
            if (packageManager.getLaunchIntentForPackage(cursor.getString(cursor.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME)))
                    != null &&
                    getIdByApplicationPackage(cursor.getString(cursor.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME)), this) != 0) {

                list.add(cursor.getString(cursor.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME)));
            } else {
                deleteUninstalledAppPackageFromRecentAppsList(cursor.getString(cursor.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME)));
            }
        }
        recentlyItemsCount = list.size();
        cursor.close();
        database.close();
        return list;
    }

    private void deleteUninstalledAppPackageFromRecentAppsList(String packageForDelete) {
        DataBase.FeedReaderDb dbHelper = new DataBase.FeedReaderDb(getApplicationContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            int id = getIdByApplicationPackage(packageForDelete, getApplicationContext());
            database.execSQL("DELETE FROM " + DataBase.FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME + " WHERE "
                    + DataBase.FeedEntry.COLUMN_NAME_FKAPPLICATIONID + "= '" + id + "'");


        } catch (Exception ec) {
            Log.e("SQL Error", Objects.requireNonNull(ec.getMessage()));
        }

    }

    public void recentlyLaunchedAppsShow() {
        ImageView[] imgs = {findViewById(R.id.img1), findViewById(R.id.img2), findViewById(R.id.img3), findViewById(R.id.img4)};
        TextView[] txt = {findViewById(R.id.txt1), findViewById(R.id.txt2), findViewById(R.id.txt3), findViewById(R.id.txt4)};

        String name = "";
        final List<String> list = getRecentlyApps(getApplicationContext());
        try {
            final PackageManager packageManager = getPackageManager();

            //Reset
            imgs[0].setVisibility(View.INVISIBLE);
            imgs[1].setVisibility(View.INVISIBLE);
            imgs[2].setVisibility(View.INVISIBLE);
            imgs[3].setVisibility(View.INVISIBLE);

            txt[0].setVisibility(View.INVISIBLE);
            txt[1].setVisibility(View.INVISIBLE);
            txt[2].setVisibility(View.INVISIBLE);
            txt[3].setVisibility(View.INVISIBLE);
            txt[0].setText(".");
            txt[1].setText(".");
            txt[2].setText(".");
            txt[3].setText(".");
            recentlyTextViews = txt;

            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    final String pkg = list.get(i);
                    name = packageManager.getApplicationLabel(packageManager.getApplicationInfo(list.get(i), 0)).toString();

                    imgs[i].setVisibility(View.VISIBLE);
                    txt[i].setVisibility(View.VISIBLE);
                    imgs[i].setImageDrawable(packageManager.getApplicationIcon(
                            pkg));

                    if (name.length() > 9) {
                        name = name.subSequence(0, 8).toString() + "...";
                    }
                    txt[i].setText(name);

                    imgs[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            ApplySettings applySettings = new ApplySettings(pkg, getApplicationContext(), dateFormat.format(new Date()));
                            startActivity(packageManager.getLaunchIntentForPackage(
                                    pkg));
                            applySettings.execute();
                        }
                    });

                }
                name = null;

                txt = null;


            }

        } catch (PackageManager.NameNotFoundException ex) {
            System.err.println(ex.getMessage());
        } finally {
            if (list.size() > 0) {
                list.clear();
            }
        }
    }

    public void clearRamMemory(View view) {

        if (appController.isUserPremium()) {
            BoostPhone ram = new BoostPhone(getApplicationContext());
            ram.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
            } else {
                createInterstitialAd();
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
            BoostPhone ram = new BoostPhone(getApplicationContext());
            ram.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }


    @SuppressLint("SimpleDateFormat")
    private AppDetail getAppByPackageName(final String pkgName) {
        final PackageManager pkm = getPackageManager();
        AppDetail appDetail = null;

        try {
            if (pkm.getLaunchIntentForPackage(pkgName) != null) {
                //Defines the play button click listener

                dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                appDetail = new AppDetail(pkm.getApplicationLabel(pkm.getApplicationInfo(pkgName, 0)).toString(),

                        pkm.getApplicationIcon(pkgName), pkgName, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (pkm.getLaunchIntentForPackage(pkgName) != null) {//Play button event
                            System.err.println("Package name apply settings: " + pkgName);
                            ApplySettings applySettings = new ApplySettings(pkgName, getApplicationContext()
                                    , dateFormat.format(new Date()));


                            Intent intent = pkm.getLaunchIntentForPackage(pkgName);
                            applySettings.execute();
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Settings Applied!", Toast.LENGTH_SHORT).show();
                            // applySettings.cancel(true);
                        }
                    }
                }, new View.OnClickListener() {//AutoTask button event
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(getApplicationContext(), AutoTasks_Activity.class);
                        intent.putExtra("pkgNameForSettings", pkgName);
                        try {
                            intent.putExtra("appName", pkm.getApplicationLabel(pkm.getApplicationInfo(pkgName, 0)));
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        BoostPhone memory = new BoostPhone(getApplicationContext());

                        if (memory.formatSize(memory.calculateTotalRamMemory()) >= 2.01) {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_out);
                            animation.setDuration(220);
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            view.findViewById(R.id.btnSettings).startAnimation(animation);

                        } else {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                        }

                    }
                });
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appDetail;
    }

    private List<AppDetail> getMyGamesInDataBase() {
        DataBase.FeedReaderDb dbHelper = new DataBase.FeedReaderDb(getApplicationContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String[] projection = {DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME};
        String selection = DataBase.FeedEntry.COLUMN_NAME_ISACTIVE + " <> -1";//Is active ?

        Cursor c = database.query(DataBase.FeedEntry.TABLE_APPLICATIONS_NAME, projection, selection, null, null, null, null);

        while (c.moveToNext()) {
            if (getAppByPackageName(c.getString(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME))) != null) {
                appDetailList.add(getAppByPackageName(c.getString(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME))));

            }

        }

        c.close();
        database.close();
        return appDetailList;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateList() {
        final PackageManager pkm = getPackageManager();
        for (int i = 0; i < appDetailList.size(); i++) {

            if (pkm.getLaunchIntentForPackage(appDetailList.get(i).getPackName()) == null) {
                appDetailList.remove(i);
                i = i - 1;
            }
        }
        adpter.notifyDataSetChanged();
    }

    //shows the bottom sheet dialog
    public void buyPremiumDialogShow(View view) {

        if (getCurrentApplicationTheme() == R.style.LightAppTheme) {
            bottomSheet = new BuyPremiumDialog(R.layout.premium_dialog_frame, R.style.BottomSheetDialogTheme);
            bottomSheet.show(getSupportFragmentManager(), "light");
        } else if (getCurrentApplicationTheme() == R.style.DarkAppTheme) {
            bottomSheet = new BuyPremiumDialog(R.layout.premium_dialog_frame, R.style.BottomSheetDialogThemeDark);
            bottomSheet.show(getSupportFragmentManager(), "dark");
        }


        drawer.closeDrawers();

    }

    //Opens the dialog from google play
    public void buyPremiumGooglePlay(View view) {
        bottomSheet.dismiss();
        AppController app = new AppController(getApplicationContext(), MainActivity.this);
        app.showPurchaseDialog();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(getCurrentApplicationTheme());
        overridePendingTransition(0, 0);
        setActivityTransitionAnimation();
        setContentView(R.layout.activity_main);
        // appController.saveTokenToServerExecute();//For debugging
        appController = new AppController(getApplicationContext(), this);
        initAds();
        appController.verifyPremium();
        createInterstitialAd();

        //Database init
        initGlobalSettingsInDataBase();
        initRecentlyLauchedApps();

        //User conifig init
        initRatingDialog();


        //init
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myRecyclerView = findViewById(R.id.myRecyclerView);
        recyclerBack = findViewById(R.id.recyclerBackground);
        txtMemoryCleaned = findViewById(R.id.txtMemoryCleaned);
        progressBarCleaner = findViewById(R.id.progressBarCleaner);
        txtNavBuyPremium = findViewById(R.id.nav_buy_premium);
        LinearLayout linearBuyPremium = findViewById(R.id.linearBuyPremium);

        if (appController.isUserPremium()) {
            txtNavBuyPremium.setClickable(false);
            txtNavBuyPremium.setText("You are Premium!");
            linearBuyPremium.setClickable(false);
        }


        //Recycler View
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        myRecyclerView.addItemDecoration(new VerticalSpaceHeight(35));
        myRecyclerView.setLayoutManager(layoutManager);
        myRecyclerView.setAdapter(adpter = new MyGamesListAdpter(getMyGamesInDataBase(), getApplicationContext(), MainActivity.this));
        // myRecyclerView.setHasFixedSize(false);


        if (Objects.requireNonNull(myRecyclerView.getAdapter()).getItemCount() > 0) {
            myRecyclerView.setVisibility(View.VISIBLE);
            recyclerBack.setVisibility(View.GONE);
        } else {
            myRecyclerView.setVisibility(View.GONE);
            recyclerBack.setVisibility(View.VISIBLE);
        }

        //Floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSelectGamesActivity();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (getCurrentApplicationTheme() == R.style.LightAppTheme) {
            toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#9717F8"));
            adpter.textColor = "#2D2C2C";
        } else {
            toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
            adpter.textColor = "#FFFFFF";
        }
        // toolbar.startAnimation(layoutAnim);//TOOLBAR ANIMATION HERE
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onStart() {
        super.onStart();
        updateList();
        recentlyLaunchedAppsShow();
        //appController.saveTokenToServerExecute();//For debugging
        //appController.executeVerifyPremium();//For debugging
    }

    @Override
    protected void onResume() {
        super.onResume();
        appController.verifyPurchases();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appDetailList.clear();
        //System.gc();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        themeIcon = menu;
        if (getCurrentApplicationTheme() == R.style.LightAppTheme) {
            menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_settings));
            themeIcon.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_sun));
        } else {
            menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_settings_white));
            themeIcon.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_moon));
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            startActivity(new Intent(getApplicationContext(), AutoTasks_Activity.class));

            return true;
        }

        if (id == R.id.theme_icon) {

            //createIntroCircularTransitionAnimation(toolbar,300);
            alterTheme();
            // database.delete(DataBase.FeedEntry.TABLE_SETTINGS_NAME,null,null);
            //database.delete(DataBase.FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME,null,null);
            //database.delete(DataBase.FeedEntry.TABLE_APPLICATIONS_NAME,null,null);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, "Hey! check out Speed Up game booster on Google Play Store, it's free :)\n " +
                    "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            share.setType("text/plain");
            Intent start = Intent.createChooser(share, null);
            startActivity(start);

        } else if (id == R.id.nav_rate) {
            String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(rate);
        } else if (id == R.id.nav_feed_back) {
            Intent send = new Intent(Intent.ACTION_SENDTO);
            send.setData(Uri.parse("mailto:softsystemstechnology@gmail.com")); // only email apps should handle this
            send.putExtra(Intent.EXTRA_SUBJECT, "Feedback-Speed Up");
            if (send.resolveActivity(getPackageManager()) != null) {
                startActivity(send);
            }

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemDeleted() {
        recentlyLaunchedAppsShow();
        //adpter.notifyItemRangeChanged(0,adpter.getItemCount());
    }


    public class BoostPhone extends AsyncTask<Void, Void, Void> {

        Context context;


        BoostPhone(Context context) {
            this.context = context;
        }

        private void killBackgroundProcess() {
            PackageManager pkg = context.getPackageManager();
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            assert am != null;
            long inicialRamMemoryAvaliable;
            long totalMemoryCleaned = 0;//Guarda o estado da memoria enquanto esta fazendo a repetição

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    animateTransition(context, txtMemoryCleaned, R.anim.fade_out, null);
                    animateTransition(context, progressBarCleaner, R.anim.fade_in, null);
                }
            });


            for (ApplicationInfo appinfo : pkg.getInstalledApplications(PackageManager.GET_META_DATA)) {

                //Verifica se o app é não é do sistema
                if (pkg.getLaunchIntentForPackage(appinfo.packageName) != null && !appinfo.packageName.equals(context.getPackageName())) {
                    inicialRamMemoryAvaliable = calculateAvaliableRamMemory();//Calcula a quantidade inicial de memoria.
                    am.killBackgroundProcesses(appinfo.packageName);//Mata um processo em background
                    long tempMemory = calculateAvaliableRamMemory();//Armazena temporariamente a quantidade atual de Memoria RAM

                    //Se caso a quantidade de memoria RAM inicial for menor que a quantidade atual some 0 a variavel
                    //Se não faça o calculo de quanta memoria foi liberada e some a variavel
                    /*Obs:Como a quantidade de memoria RAM do aparelho varia quase o tempo 1todo, pode ser que a quantidade
                     * final seja seja maior que a quantidade inicial, e por isso é necessario fazer o teste abaixo, para
                     * exitar que o calculo resulte em valores negativos.
                     *
                     *
                     * */
                    totalMemoryCleaned += (inicialRamMemoryAvaliable < tempMemory) ? 0 : inicialRamMemoryAvaliable - tempMemory;


                }

            }
            final String totalCleared = formatSize(totalMemoryCleaned, true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    animateTransition(context, progressBarCleaner, R.anim.fade_out, null);
                    animateTransition(context, txtMemoryCleaned, R.anim.fade_in, null);
                    txtMemoryCleaned.setText(totalCleared);

                }
            });

        }

        private long calculateAvaliableRamMemory() {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            assert activityManager != null;
            activityManager.getMemoryInfo(mi);
            return mi.availMem;
        }

        private long calculateTotalRamMemory() {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            assert activityManager != null;
            activityManager.getMemoryInfo(mi);
            return mi.totalMem;
        }

        private String formatSize(long size, boolean isBinary) {
            double n = 0;
            if (isBinary) {
                n = 1024;
            } else {
                n = 1000;
            }
            String s = "";
            double kb = size / n;
            double mb = kb / n;
            double gb = mb / n;
            double tb = gb / n;
            System.err.println("Sizes: " + kb + " kb, " + mb + " mb" + gb + " GB");
            if (size < n) {
                s = size + " MB"; //Este resultado esta em Bytes
            } else if (size >= n && size < (n * n)) {
                s = String.format(Locale.US, "%.2f", kb) + " KB";
            } else if (size >= (n * n) && size < (n * n * n)) {
                s = String.format(Locale.US, "%.2f", mb) + " MB";
            } else if (size >= (n * n * n) && size < (n * n * n * n)) {
                s = String.format(Locale.US, "%.2f", gb) + " GB";
            } else if (size >= (n * n * n * n)) {
                s = String.format(Locale.US, "%.2f", tb) + " TB";
            }


            return s;
        }

        private double formatSize(long size) {
            double n = 0;
            n = 1024;

            double kb = size / n;
            double mb = kb / n;
            double gb = mb / n;
            double tb = gb / n;
            if (size < n) {
                return size;
            } else if (size >= n && size < (n * n)) {
                return kb;
            } else if (size >= (n * n) && size < (n * n * n)) {
                return mb;
            } else if (size >= (n * n * n) && size < (n * n * n * n)) {
                return gb;
            } else if (size >= (n * n * n * n)) {
                return tb;
            }

            return size;
        }

        private long calculateTotalInternalStorageSize() {
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            //long blockSize=stat.getBlockSizeLong();
            //long totalBlocks=stat.getBlockCountLong();
            return stat.getBlockCountLong() * stat.getBlockSizeLong();
        }

        private long calculateAvaliableDiskSpace() {
            return new File(getApplicationContext().getFilesDir().getAbsoluteFile().toString()).getFreeSpace();
        }

        private int calculateRamMemoryInUse() {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            assert activityManager != null;
            activityManager.getMemoryInfo(mi);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Math.toIntExact((mi.totalMem - mi.availMem));
            } else {
                return (int) ((mi.totalMem - mi.availMem) * 100 / mi.totalMem);
            }


        }

        @Override
        protected Void doInBackground(Void... voids) {


            try {
                killBackgroundProcess();

            } catch (Exception ex) {
                ex.printStackTrace();
            }


            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.cancel(true);

        }
    }


    public static class ApplySettings extends AsyncTask<Void, Void, Void> {

        private String pkgName;
        private int application_id;
        String datetime = "";

        private AutoTaskSetting autoTaskSetting;
        @SuppressLint("StaticFieldLeak")
        public Context context;

        ApplySettings(String pkgName, Context context, String datetime) {
            this.pkgName = pkgName;
            this.context = context;
            this.datetime = datetime;

        }

        ApplySettings(String pkgName, Context context) {
            this.pkgName = pkgName;
            this.context = context;
        }


        private AutoTaskSetting getApplicationAutoTasks(String pkgName) {
            DataBase.FeedReaderDb dbHelper = new DataBase.FeedReaderDb(context);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            final String SELECTION = "SELECT * FROM " + DataBase.FeedEntry.TABLE_APPLICATIONS_NAME +
                    " INNER JOIN auto_task_settings ON applications.FK_settingsId=auto_task_id WHERE "
                    + DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME + "= '" + pkgName + "' ";

            Cursor c = database.rawQuery(SELECTION, null);
            c.moveToFirst();
            if (c.getCount() > 0 && c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE)) == 1) {

                application_id = c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_APPLICATION_ID));
                autoTaskSetting = new AutoTaskSetting(c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE)),
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
            } else {
                c = database.rawQuery("SELECT * FROM " + DataBase.FeedEntry.TABLE_SETTINGS_NAME + " WHERE "
                        + DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING + "= 1", null);
                c.moveToFirst();

                autoTaskSetting = new AutoTaskSetting(c.getInt(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE)),
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
            database.close();
            return autoTaskSetting;
        }

        private void addRecentlyLaunchedApp(int application_id, String datetime) {
            DataBase.FeedReaderDb dbHelper = new DataBase.FeedReaderDb(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            String filter = "";
            //If the row contains the same package that will be inserted, do nothing
            ContentValues values = new ContentValues();
            if (recentlyItemsCount < 4) {
                values.put(DataBase.FeedEntry.COLUMN_NAME_DATETIMELAUNCHED, datetime);
                values.put(DataBase.FeedEntry.COLUMN_NAME_FKAPPLICATIONID, application_id);
                database.insert(DataBase.FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME, null, values);

            } else {
                database = dbHelper.getReadableDatabase();

                //Confere se o registro a ser atualizado já existe na tabela
                Cursor cursor = database.rawQuery("SELECT " + DataBase.FeedEntry.COLUMN_NAME_FKAPPLICATIONID + " FROM "
                        + DataBase.FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME + " WHERE "
                        + DataBase.FeedEntry.COLUMN_NAME_FKAPPLICATIONID + "= ?", new String[]{String.valueOf(application_id)});

                if (cursor.getCount() > 0) {//Se o registro ja existe na tabela, atualize o mesmo registro
                    filter = DataBase.FeedEntry.COLUMN_NAME_FKAPPLICATIONID + "=" + application_id;
                } else {//Se o registro não existe na tabela atualize o registro onde o date time for mais antigo(qualquer um)
                    filter = DataBase.FeedEntry.COLUMN_NAME_DATETIMELAUNCHED + "=(select launch_date_time from recently_applications order by launch_date_time asc limit 1)";

                }
                cursor.close();
                database = dbHelper.getWritableDatabase();
                values.put(DataBase.FeedEntry.COLUMN_NAME_DATETIMELAUNCHED, datetime);
                values.put(DataBase.FeedEntry.COLUMN_NAME_FKAPPLICATIONID, application_id);
                database.update(DataBase.FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME, values,
                        filter, null);

            }

            database.close();

        }


        void applySettings(AutoTaskSetting setting) {

            AudioManager volume;
            WifiManager wifi;
            BluetoothManager bluetooth;
            LocationManager location;


            //WiFi
            if (setting.getWifi_enable() == 1) {

                wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                assert wifi != null;
                wifi.setWifiEnabled(true);
            } else if (setting.getWifi_enable() == -1) {
                wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                assert wifi != null;
                wifi.setWifiEnabled(false);
            }

            //Bluetooth
            if (setting.getEnable_bluetooth() == 1) {
                bluetooth = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        bluetooth.getAdapter().enable();
                    }
                }
                else {
                    bluetooth.getAdapter().enable();
                }
            }
            else if(setting.getEnable_bluetooth()==-1) {
                bluetooth=(BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                if (bluetooth != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                            bluetooth.getAdapter().disable();
                        }
                    }
                    else {
                        bluetooth.getAdapter().disable();
                    }
                }
            }

            //GPS
            if(setting.getEnable_gps()==1){

                location=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                assert location != null;
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
            else if(setting.getEnable_gps()==-1){
                location=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                assert location != null;
                //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }


            //BRIGHTNESS
            if(setting.getBrightness()>-1) {
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

                Settings.System.putInt(
                        context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS,
                        setting.getBrightness());

            }

            if(setting.getMedia_volume()>-1){
                volume=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                assert volume != null;
                System.err.println("Current Media volume: "+volume.getStreamVolume(AudioManager.STREAM_MUSIC));
                System.err.println("Media volume: "+setting.getMedia_volume());
                volume.setStreamVolume(AudioManager.STREAM_MUSIC,setting.getMedia_volume(),AudioManager.FLAG_SHOW_UI);
                System.err.println("After Media volume: "+setting.getMedia_volume());

            }

            if(setting.getCall_volume()>-1){
                volume=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                assert volume != null;
                System.err.println("Current Call volume: "+volume.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
                System.err.println("Call volume: "+setting.getCall_volume());
                volume.setStreamVolume(AudioManager.STREAM_VOICE_CALL,setting.getCall_volume(),AudioManager.FLAG_SHOW_UI);
                System.err.println("After Call volume: "+volume.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
            }

            if(setting.getRing_volume()>-1){
                volume=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                assert volume != null;
                System.err.println("Current Ring volume: "+volume.getStreamVolume(AudioManager.STREAM_RING));
                System.err.println("Ring volume: "+setting.getRing_volume());
                volume.setStreamVolume(AudioManager.STREAM_RING,setting.getRing_volume(),AudioManager.FLAG_SHOW_UI);
                System.err.println("After Call volume: "+volume.getStreamVolume(AudioManager.STREAM_RING));
            }

            if(setting.getEnable_notification_block()==1){
                Intent  notificationServiceIntent=new Intent(context,NotificationListener.class);
                notificationServiceIntent.putExtra("isListenerActive",true);
                context.startService(notificationServiceIntent);
            }

            if(setting.getEnable_block_call()==1){

                IntentFilter filter=new IntentFilter();
                filter.addAction("com.ikkeware.PHONE_LISTENER_STATE");
                context.registerReceiver(new PhoneCallBlockerServiceReceiver(),filter);
                context.sendBroadcast(new Intent("com.ikkeware.PHONE_LISTENER_STATE"));
            }
            SharedPreferences sharedPreferences=context.getSharedPreferences("autoTaskSettings",MODE_PRIVATE);


            if(sharedPreferences.contains("boost"+pkgName) && sharedPreferences.getBoolean("boost"+pkgName,false) ) {

                Intent intent = new Intent(context, InGameBoostService.class);
                intent.putExtra("mode", sharedPreferences.getInt("boostSeekBar" + pkgName, 0));
                intent.putExtra("pkgName", pkgName);
                context.startService(intent);


            }




        }

        @Override
        protected Void doInBackground(Void... voids) {
            applySettings(getApplicationAutoTasks(pkgName));

            application_id=getIdByApplicationPackage(pkgName,context);
            addRecentlyLaunchedApp(application_id, datetime);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cancel(true);
        }
    }






}



