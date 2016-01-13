package com.franklin.keepme.DB;

import android.provider.BaseColumns;

public class DBContract {
    //public int _id;
    //public
    public String title;
    public String description;
    public String fromTime;
    public String toTime;
    //public String location;
    public int notify;
    //public String label;

    public DBContract(String title,
                      String description,
                      String fromTime,
                      String toTime,
                      //String location,
                      int notify) {
        this.title = title;
        this.description = description;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.notify = notify;
    }

    public static abstract class DBEntry implements BaseColumns {
        public static final String TABLE_NAME = "item";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_FROM_TIME = "from_time";
        public static final String COLUMN_NAME_TO_TIME = "to_time";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_NOTIFY = "notify";
        public static final String COLUMN_NAME_LABEL = "label";
    }
}
