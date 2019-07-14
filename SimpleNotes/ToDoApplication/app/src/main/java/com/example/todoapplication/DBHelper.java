package com.example.todoapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    DBHelper(Context context){ super(context, "dbHelper", null, 1);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tasks("
                + "id integer primary key autoincrement,"
                + "task text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
