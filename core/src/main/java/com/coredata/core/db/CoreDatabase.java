package com.coredata.core.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by wangjinpeng on 2017/7/31.
 */

public abstract class CoreDatabase {

    public abstract void beginTransaction();

    public abstract void setTransactionSuccessful();

    public abstract void endTransaction();

    public abstract Cursor rawQuery(String sql, String[] selectionArgs);

    public abstract CoreStatement compileStatement(String sql);

    public abstract void execSQL(String sql);

    public abstract long replace(String table, String nullColumnHack, ContentValues initialValues);
}
