package com.franklin.keepme;

import com.franklin.keepme.DB.DBManager;

/**
 * Created by franklin on 2/7/16.
 */
public class Application extends android.app.Application {

    private DBManager dbManager;
    public void onCreate() {
        super.onCreate();
        dbManager = new DBManager(getApplicationContext());
    }

    public void onTerminate() {
        super.onTerminate();

    }

    public DBManager getDbManager() {
        return dbManager;
    }
}
