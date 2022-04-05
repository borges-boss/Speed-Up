package com.ikkeware.rambooster;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.ikkeware.rambooster.adapter.SelectGamesListAdapter;
import com.ikkeware.rambooster.model.AppDetail;
import com.ikkeware.rambooster.model.DataBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ikkeware.rambooster.model.AppTheme.getCurrentApplicationTheme;

public class SelectGamesActivity extends AppCompatActivity implements onListChanged {

    private RecyclerView gamesList;
    public SelectGamesListAdapter selectGamesListAdapter = null;
    public DataBase.FeedReaderDb dbHelper;
    private EditText searchBox;
    private ProgressBar myProgressBar;
    private MenuItem saveButton;

    InterstitialAd interstitialAd;
    private AppController appController;
    final String UID="ca-app-pub-3549767228651381/8382759342";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(getCurrentApplicationTheme());
        setContentView(R.layout.select_games_activity);
        appController=new AppController(this,this);


        final Toolbar toolbar = findViewById(R.id.toolbar_select_games);
        setSupportActionBar(toolbar);
        //Theme set
        if (getCurrentApplicationTheme()==R.style.DarkAppTheme) {
            toolbar.setNavigationIcon(R.drawable.back_arrow_white);

        }
        else {
            toolbar.setNavigationIcon(R.drawable.back_arrow);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        toolbar.setTitleMarginStart(0);
        toolbar.setTitle("Select Games");
        dbHelper = new DataBase.FeedReaderDb(getApplicationContext());
        gamesList = findViewById(R.id.games_list_recycler_view);
        searchBox = findViewById(R.id.searchBox);
        myProgressBar=findViewById(R.id.myProgressBar);
        searchBox.setEnabled(false);
        myProgressBar.setIndeterminate(true);

        LoadGames loadGames = new LoadGames();
        loadGames.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_game, menu);
        saveButton=menu.getItem(0);
        saveButton.setEnabled(false);

        SpannableString s = new SpannableString("Save");
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s.length(), 0);

        saveButton.setTitle(s);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_games) {

            if (!selectGamesListAdapter.getGamesToAdd().isEmpty()) {
                saveButton.setEnabled(false);
                SpannableString s = new SpannableString("Save");
                s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s.length(), 0);
                saveButton.setTitle(s);//disable button on click

                saveGamesToDataBase(selectGamesListAdapter.getGamesToAdd());
                selectGamesListAdapter.appDetails.clear();
                selectGamesListAdapter.test.clear();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }


            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemsChanged(){

        if(selectGamesListAdapter.getGamesToAdd().isEmpty()){
            saveButton.setEnabled(false);
            SpannableString s = new SpannableString("Save");
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s.length(), 0);
            saveButton.setTitle(s);
        }
        else{
            saveButton.setEnabled(true);
            SpannableString s = new SpannableString("Save");
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#A227FF")), 0, s.length(), 0);
            saveButton.setTitle(s);
        }
    }

    @Override
    public void onContentFinishLoading() {
        myProgressBar.setVisibility(View.GONE);
    }

    //Por padrao os jogos salvos no Db terão o id da configuraçao global. Que podera ser atualizado posteriormente
    private void saveGamesToDataBase(List<String> gamesToAdd)  {//Por default a configuraçao global vai ser salva no aplicativo adicionado
        int id_global_setting = 0;
        DataBase.FeedReaderDb dbHelper = new DataBase.FeedReaderDb(getApplicationContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor c = database.rawQuery("SELECT " + DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_ID + " FROM " + DataBase.FeedEntry.TABLE_SETTINGS_NAME
                + " WHERE " + DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING + "=1", null);

        c.moveToFirst();
        if (c.getCount() != 0) {
            id_global_setting = c.getInt(0);
        }
        c.close();
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (int i = 0; i < gamesToAdd.size(); i++) {
            values.put(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME, gamesToAdd.get(i));
            values.put(DataBase.FeedEntry.COLUMN_NAME_ISACTIVE, 1);
            values.put(DataBase.FeedEntry.COLUMN_NAME_FKSETTING, id_global_setting);
            database.insert(DataBase.FeedEntry.TABLE_APPLICATIONS_NAME, null, values);

        }

        database.close();
        values.clear();

    }

    private Filter getFilter(){
        return filter;
    }


    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        }
    };


    public class LoadGames extends AsyncTask<Void, Void, Void> {
        SQLiteDatabase database;
        Cursor c;
        int progressUpdate=0;

        //Este metodo tem o objetivo de obeter todos os apps instalados no aparelho, excluindo os que ja estao adicionados no DB
        private List<AppDetail> getInstalledAppsFiltered(List<AppDetail> appDetails) {
            database = dbHelper.getReadableDatabase();

            Map<String, AppDetail> values = new HashMap<>();
            String[] proje = {DataBase.FeedEntry.COLUMN_NAME_APPLICATION_ID, DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME, DataBase.FeedEntry.COLUMN_NAME_FKSETTING};

            for (int i = 0; i < appDetails.size(); i++) {
                values.put(appDetails.get(i).getPackName(), appDetails.get(i));
            }

            c = database.query(DataBase.FeedEntry.TABLE_APPLICATIONS_NAME, proje, null, null, null, null, null);

            while (c.moveToNext()) {
                if (c.getString(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME)) != null &&
                        values.containsKey(c.getString(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME)))) {

                    appDetails.remove(values.get(c.getString(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME))));
                    values.remove(c.getString(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME)));

                    System.err.println("FK_SETTING " + c.getString(c.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_FKSETTING)));
                }

            }
            List<AppDetail> appDetailsList = appDetails;
            appDetails = null;

            c.close();
            values.clear();
            database.close();

            return appDetailsList;
        }


        private List<AppDetail> getInstalledApps() {
            final PackageManager pkm = getPackageManager();
            List<ApplicationInfo> pacotes = pkm.getInstalledApplications(PackageManager.GET_META_DATA);
            List<AppDetail> appDetails = new ArrayList<>();

            for (int i = 0; i < pacotes.size(); i++) {
                try {//Application name and icon
                    if (pkm.getLaunchIntentForPackage(pacotes.get(i).packageName) != null) {
                        appDetails.add(new AppDetail(pkm.getApplicationLabel(pkm.getApplicationInfo(pacotes.get(i).packageName, 0)).toString()
                                , pkm.getApplicationIcon(pacotes.get(i).packageName), pacotes.get(i).packageName));
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return appDetails;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            selectGamesListAdapter = new SelectGamesListAdapter(getInstalledAppsFiltered(getInstalledApps())
                    ,SelectGamesActivity.this,SelectGamesActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gamesList.addItemDecoration(new VerticalSpaceHeight(35));
                    gamesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    if (selectGamesListAdapter != null) {
                        gamesList.setAdapter(selectGamesListAdapter);

                        //Enable SearchBox after load all games
                        searchBox.setEnabled(true);

                        searchBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                myProgressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                Log.d("Text changed",editable.toString());
                                selectGamesListAdapter.getFilter().filter(editable.toString(), new Filter.FilterListener() {
                                    @Override
                                    public void onFilterComplete(int i) {
                                        myProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });



                        myProgressBar.setVisibility(View.GONE);
                        gamesList.setVisibility(View.VISIBLE);
                    }
                    cancel(true);
                }
            });

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myProgressBar.setProgress(progressUpdate++);

                }
            });
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(database!=null){ database.close(); }
                    if(c!=null){ c.close(); }
                }
            });
        }


    }
}