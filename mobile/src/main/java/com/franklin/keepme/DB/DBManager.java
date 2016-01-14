package com.franklin.keepme.DB;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import com.franklin.keepme.DB.DBContract;
import com.franklin.keepme.DB.DBContract.DBEntry;
import com.franklin.keepme.DB.DBHelper;
import com.franklin.keepme.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DBManager {

    final String[] columns = {DBEntry.COLUMN_NAME_TITLE, DBEntry.COLUMN_NAME_FROM_TIME, DBEntry.COLUMN_NAME_TO_TIME};
    final String selection = "from_time >= ? AND from_time <= ?";
    final String orderBy = DBEntry.COLUMN_NAME_FROM_TIME + " ASC ";
    final String deleteWhereClause = "_id = ?";

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

    public List<DBContract> retrieveData(String date) {

        String[] selectionArgs = new String[2];
        selectionArgs[0] = date + " 00:00:00";
        selectionArgs[1] = date + " 23:59:59";

        Cursor cursor = db.query(DBEntry.TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
        List<DBContract> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            DBContract data = new DBContract(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getInt(5));
            result.add(data);
        }
        return result;
    }

    public void deleteData(int _id) {
        String[] args = new String[1];
        args[0] = Integer.toString(_id);
        db.delete(DBEntry.TABLE_NAME, deleteWhereClause, args);
    }
}
