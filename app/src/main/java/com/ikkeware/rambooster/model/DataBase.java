package com.ikkeware.rambooster.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBase {

    private DataBase(){}


    public static class FeedEntry implements BaseColumns{

        public static final String TABLE_APPLICATIONS_NAME = "applications";
        public static final String COLUMN_NAME_APPLICATION_ID="application_id";
        public static final String COLUMN_NAME_PACKAGENAME = "package_name";
        public static final String COLUMN_NAME_FKSETTING="FK_settingsId";
        public static final String COLUMN_NAME_ISACTIVE = "is_active";


        public static final String TABLE_SETTINGS_NAME="auto_task_settings";
        public static final String COLUMN_NAME_AUTO_TASK_ID="auto_task_id";
        public static final String COLUMN_NAME_ENABLE_WIFI = "enable_wifi";
        public static final String COLUMN_NAME_DATA = "enable_mobile_data";
        public static final String COLUMN_NAME_BLUETOOTH = "enable_bluetooth";
        public static final String COLUMN_NAME_GPS = "enable_gps";
        public static final String COLUMN_NAME_BRIGHTNESS = "brightness";
        public static final String COLUMN_NAME_MEDIA_VOLUME = "media_volume";
        public static final String COLUMN_NAME_CALL_VULUME = "call_volume";
        public static final String COLUMN_NAME_RING_VULUME = "ring_volume";
        public static final String COLUMN_NAME_BLOCK_CALL = "enable_block_call";
        public static final String COLUMN_NAME_BLOCK_NOTIFICATION = "enable_notification_block";
        public static final String COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE = "is_setting_active";
        public static final String COLUMN_NAME_IS_GLOBAL_SETTING="is_global_setting";

        public static final String TABLE_RECENTLY_APPLICATIONS_NAME="recently_applications";
        public static final String COLUMN_NAME_RECENTLYID="recently_id";
        public static final String COLUMN_NAME_FKAPPLICATIONID="FK_application_id";
        public static final String COLUMN_NAME_DATETIMELAUNCHED="launch_date_time";

        /*VALUES
               -1=NO ACTION
               0=DISABLE
               1=ACTIVE
               */
        public static final String INSERT_GLOBAL_CONFIG="INSERT INTO "+FeedEntry.TABLE_SETTINGS_NAME+" ("
                +FeedEntry.COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE+","
                +FeedEntry.COLUMN_NAME_ENABLE_WIFI+","
                +FeedEntry.COLUMN_NAME_DATA+", "
                +FeedEntry.COLUMN_NAME_BLUETOOTH+", "
                +FeedEntry.COLUMN_NAME_GPS+", "
                +FeedEntry.COLUMN_NAME_BRIGHTNESS+", "
                +FeedEntry.COLUMN_NAME_MEDIA_VOLUME+", "
                +FeedEntry.COLUMN_NAME_CALL_VULUME+", "
                +FeedEntry.COLUMN_NAME_RING_VULUME+","
                +FeedEntry.COLUMN_NAME_BLOCK_CALL+","
                +FeedEntry.COLUMN_NAME_BLOCK_NOTIFICATION+", "+
                FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING+") values"+"(1,0,0,0,-1,-1,-1,-1,-1,-1,-1,1);"
                ;




        //select * from applications inner join auto_task_settings on applications.FK_settingsId=auto_task_settings.auto_task_id;




    }


    public static class FeedReaderDb extends SQLiteOpenHelper{
        public static final String DATA_BASE_NAME="applications.db";
        public static final int DATA_BASE_VERSION=1;


        private final String CREATE_TABLE_RECENTLY_APPLICATIONS="CREATE TABLE IF NOT EXISTS "+FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME
                +"("+FeedEntry.COLUMN_NAME_RECENTLYID+" INTEGER PRIMARY KEY NOT NULL, "
                +FeedEntry.COLUMN_NAME_FKAPPLICATIONID+" INTEGER, "
                +FeedEntry.COLUMN_NAME_DATETIMELAUNCHED+" TEXT, "
                +"FOREIGN KEY("+FeedEntry.COLUMN_NAME_FKAPPLICATIONID+") REFERENCES "+FeedEntry.COLUMN_NAME_APPLICATION_ID + ")";

        private final String CREATE_TABLE_APPLICATIONS="CREATE TABLE IF NOT EXISTS "+FeedEntry.TABLE_APPLICATIONS_NAME+"("+
                FeedEntry.COLUMN_NAME_APPLICATION_ID+" INTEGER PRIMARY KEY NOT NULL,"+
                FeedEntry.COLUMN_NAME_PACKAGENAME+" TEXT,"+
                FeedEntry.COLUMN_NAME_FKSETTING+" INTEGER,"+
                FeedEntry.COLUMN_NAME_ISACTIVE+" INTEGER,"+
                "FOREIGN KEY("+FeedEntry.COLUMN_NAME_FKSETTING+") REFERENCES "+FeedEntry.COLUMN_NAME_AUTO_TASK_ID + ")";

        private final String CREATE_TABLE_SETTINGS="CREATE TABLE IF NOT EXISTS "+FeedEntry.TABLE_SETTINGS_NAME+" ("+
                FeedEntry.COLUMN_NAME_AUTO_TASK_ID+" INTEGER PRIMARY KEY NOT NULL,"+
                FeedEntry.COLUMN_NAME_AUTO_TASK_SETTING_IS_ACTIVE+" INTEGER,"+
                FeedEntry.COLUMN_NAME_ENABLE_WIFI+" INTEGER,"
                +FeedEntry.COLUMN_NAME_DATA+" INTEGER,"
                +FeedEntry.COLUMN_NAME_BLUETOOTH+
                " INTEGER, "+FeedEntry.COLUMN_NAME_GPS+" INTEGER,"
                +FeedEntry.COLUMN_NAME_BRIGHTNESS+
                " INTEGER, "+FeedEntry.COLUMN_NAME_MEDIA_VOLUME+" INTEGER,"
                +FeedEntry.COLUMN_NAME_CALL_VULUME+" INTEGER,"+
                FeedEntry.COLUMN_NAME_RING_VULUME+" INTEGER,"+FeedEntry.COLUMN_NAME_BLOCK_CALL+
                " INTEGER,"+FeedEntry.COLUMN_NAME_BLOCK_NOTIFICATION+ " INTEGER,"
                +FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING+" INTEGER)";





        public FeedReaderDb(Context context) {
            super(context,DATA_BASE_NAME,null,DATA_BASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_SETTINGS);
            sqLiteDatabase.execSQL(CREATE_TABLE_APPLICATIONS);
           sqLiteDatabase.execSQL(CREATE_TABLE_RECENTLY_APPLICATIONS);
                Log.v("DATABASE","create tables");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
           // sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+FeedEntry.TABLE_SETTINGS_NAME);
            //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME);
            //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+FeedEntry.TABLE_APPLICATIONS_NAME);
            //this.onCreate(sqLiteDatabase);

        }
    }
}
