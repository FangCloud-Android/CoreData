package com.wanpg.core.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wanpg.core.CoreObject;

import java.util.List;

/**
 * Created by wangjinpeng on 2017/3/21.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private List<Class<? extends CoreObject>> classList;

    public DataBaseHelper(Context context, String name, int version, List<Class<? extends CoreObject>> classList) {
        this(context, name, null, version);
        this.classList = classList;
    }

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Class<? extends CoreObject> clazz : classList) {
            db.execSQL(SqlBuilder.buildCreateSql(clazz, true));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        super.onDowngrade(db, oldVersion, newVersion);
    }
}
