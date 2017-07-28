package com.coredata.core.helper;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.coredata.core.CoreData;
import com.coredata.core.CoreDatabaseManager;
import com.coredata.core.db.CoreDatabase;

/**
 * CoreData绑定Sqlite3的Helper
 *
 * @author wangjinpeng
 */
public class NormalHelper extends SQLiteOpenHelper {

    public NormalHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public NormalHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CoreDatabaseManager cdbManager = CoreData.defaultInstance().getCoreDataBase();
        cdbManager.onCreate(new CoreDatabase(db));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CoreDatabaseManager cdbManager = CoreData.defaultInstance().getCoreDataBase();
        cdbManager.onUpgrade(new CoreDatabase(db), oldVersion, newVersion);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CoreDatabaseManager cdbManager = CoreData.defaultInstance().getCoreDataBase();
        cdbManager.onDowngrade(new CoreDatabase(db), oldVersion, newVersion);
    }
}
