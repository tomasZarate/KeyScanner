package com.tdp.protoscan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDPassword extends SQLiteOpenHelper {

    private static final String COMMENTS_TABLE_CREATE = "CREATE TABLE comments(_id INTEGER PRIMARY KEY AUTOINCREMENT, user TEXT, comment TEXT)";
    private static final String DB_NAME = "comments.sqlite";
    private static final int DB_VERSION = 1;

    public BDPassword(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COMMENTS_TABLE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
