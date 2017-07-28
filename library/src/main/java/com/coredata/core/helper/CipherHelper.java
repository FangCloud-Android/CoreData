package com.coredata.core.helper;

import android.content.Context;

import com.coredata.core.CoreData;
import com.coredata.core.CoreDatabaseManager;
import com.coredata.core.db.CoreDatabase;

import net.sqlcipher.DatabaseErrorHandler;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * CoreData绑定Sqlite3的Helper
 *
 * @author wangjinpeng
 */
public class CipherHelper extends SQLiteOpenHelper {

    public CipherHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CipherHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, SQLiteDatabaseHook hook) {
        super(context, name, factory, version, hook);
    }

    public CipherHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, SQLiteDatabaseHook hook, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, hook, errorHandler);
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
}
