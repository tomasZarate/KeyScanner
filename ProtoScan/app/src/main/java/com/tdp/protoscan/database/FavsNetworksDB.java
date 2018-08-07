package com.tdp.protoscan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavsNetworksDB extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "FavsNetworks.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WifiNetworkContract.FeedEntry.TABLE_NAME + " (" +
                    WifiNetworkContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    WifiNetworkContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    WifiNetworkContract.FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";
    //Eliminar entradas
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WifiNetworkContract.FeedEntry.TABLE_NAME;


    public FavsNetworksDB (Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
