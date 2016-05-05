package com.franklin.keepme.DB;

import android.provider.BaseColumns;

public class DBContract {
    public long _id;
    //public
    public String title;
    public String description;
    public String fromTime;
    public String toTime;
    //public String location;
    public int notify;
    //public String label;
    public String user;
    public boolean dirty;
    public boolean delete;

    public DBContract(long _id,
                      String title,
                      String description,
                      String fromTime,
                      String toTime,
                      //String location,
                      int notify,
                      String user,
                      boolean dirty,
                      boolean delete) {
        this._id = _id;
        this.title = title;
        this.description = description;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.notify = notify;
        this.user = user;
        this.dirty = dirty;
        this.delete = delete;
    }

    public static abstract class DBEntry implements BaseColumns {
        public static final String TABLE_NAME = "item";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_FROM_TIME = "from_time";
        public static final String COLUMN_NAME_TO_TIME = "to_time";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_NOTIFY = "notify";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_LAST_MODIFIED = "last_modified";
        public static final String COLUMN_NAME_DIRTY = "dirty";
        public static final String COLUMN_NAME_DELETED = "deleted";
    }
}
