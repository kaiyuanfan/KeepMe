package com.franklin.keepme.DB;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.franklin.keepme.DB.DBContract;
import com.franklin.keepme.DB.DBContract.DBEntry;
import com.franklin.keepme.DB.DBHelper;

public class DBManager {
    private SQLiteDatabase db;

    public DBManager(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void closeDB() {
        db.close();
    }

    public void putData(DBContract data) {
        ContentValues values = new ContentValues();

        values.put(DBEntry.COLUMN_NAME_TITLE, data.title);
        values.put(DBEntry.COLUMN_NAME_DESCRIPTION, data.description);
        values.put(DBEntry.COLUMN_NAME_FROM_TIME, data.fromTime);
        values.put(DBEntry.COLUMN_NAME_TO_TIME, data.toTime);
        values.put(DBEntry.COLUMN_NAME_NOTIFY, data.notify);

        db.insert(DBEntry.TABLE_NAME, null, values);
    }

    public void retrieveDay() {

    }
}
