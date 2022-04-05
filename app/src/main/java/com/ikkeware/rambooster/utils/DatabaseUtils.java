package com.ikkeware.rambooster.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ikkeware.rambooster.model.DataBase;

public class DatabaseUtils {

    public static void deleteApplication(int applicationId, Context context){
        DataBase.FeedReaderDb dbHelper= new DataBase.FeedReaderDb(context);
        SQLiteDatabase database= dbHelper.getWritableDatabase();

        database.delete(DataBase.FeedEntry.TABLE_RECENTLY_APPLICATIONS_NAME, "FK_application_id= ?",
                new String[]{String.valueOf(applicationId)});


        database= dbHelper.getReadableDatabase();
        Cursor autoTaskSettings=database.rawQuery("SELECT * FROM "+ DataBase.FeedEntry.TABLE_APPLICATIONS_NAME+
                " INNER JOIN auto_task_settings ON applications.FK_settingsId=auto_task_id WHERE "+
                DataBase.FeedEntry.COLUMN_NAME_APPLICATION_ID+" = ? ",new String[]{String.valueOf(applicationId)});

        if(autoTaskSettings.getCount()>0){
            //Not a global setting, can delete it.
            autoTaskSettings.moveToFirst();
            if(autoTaskSettings.getInt(autoTaskSettings.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_IS_GLOBAL_SETTING))!=1){
                database=dbHelper.getWritableDatabase();

                //Delete application
                database.delete(DataBase.FeedEntry.TABLE_APPLICATIONS_NAME,
                        DataBase.FeedEntry.COLUMN_NAME_APPLICATION_ID+"= '"+applicationId+"' ",null);

                //Delete settings
                database.delete(DataBase.FeedEntry.TABLE_SETTINGS_NAME,
                        DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_ID+"= '"
                                +autoTaskSettings.getInt(autoTaskSettings.getColumnIndexOrThrow(DataBase.FeedEntry.COLUMN_NAME_AUTO_TASK_ID))+"'",null);

            }
            //Is a global setting don't delete it, just delete the application in db.
            else{
                database.delete(DataBase.FeedEntry.TABLE_APPLICATIONS_NAME,
                        DataBase.FeedEntry.COLUMN_NAME_APPLICATION_ID+"= '"+applicationId+"' ",null);
            }
            autoTaskSettings.close();
            database.close();
        }


    }

    public static int getIdByApplicationPackage(String pkg, Context context){

        DataBase.FeedReaderDb dbHelper = new DataBase.FeedReaderDb(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        final Cursor cursor=database.rawQuery("SELECT "+ DataBase.FeedEntry.COLUMN_NAME_APPLICATION_ID
                +" FROM "+ DataBase.FeedEntry.TABLE_APPLICATIONS_NAME+" WHERE "
                + DataBase.FeedEntry.COLUMN_NAME_PACKAGENAME+"= ? ",new String[]{pkg});

        int id =0;
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            id=cursor.getInt(0);
        }

        cursor.close();
        database.close();
        return id;
    }
}
