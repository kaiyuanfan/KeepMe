package com.franklin.keepme.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.franklin.keepme.DB.DBContract.DBEntry;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "KeepMe.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = " , ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBEntry.TABLE_NAME + " ( " +
                    DBEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    //DBEntry.COLUMN_NAME_UUID + BLOB_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_FROM_TIME + TEXT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_TO_TIME + TEXT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_NOTIFY + INT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_USER + TEXT_TYPE + COMMA_SEP +
                    //DBEntry.COLUMN_NAME_LABEL + TEXT_TYPE
                    //DBEntry.COLUMN_NAME_LAST_MODIFIED + INT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_DIRTY + INT_TYPE + COMMA_SEP +
                    DBEntry.COLUMN_NAME_DELETED + INT_TYPE +
                    " )";
    private static final String SQL_CREATE_INDEX =
            "CREATE INDEX date_index ON " + DBEntry.TABLE_NAME + " (" +
                    DBEntry.COLUMN_NAME_FROM_TIME +")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

