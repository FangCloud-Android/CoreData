package com.coredata.core.normal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.coredata.core.CoreData;
import com.coredata.core.CoreDatabaseManager;
import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.OpenHelperInterface;

/**
 * CoreData绑定Sqlite3的Helper
 *
 * @author wangjinpeng
 */
public class NormalOpenHelper extends SQLiteOpenHelper implements OpenHelperInterface {

    private String instanceTag;

    public NormalOpenHelper(Context context, String name, int version, String tag) {
        super(context, name, null, version);
        this.instanceTag = tag;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CoreDatabaseManager cdbManager = CoreData.instance(instanceTag).getCoreDataBase();
        cdbManager.onCreate(new NormalDatabase(db));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CoreDatabaseManager cdbManager = CoreData.instance(instanceTag).getCoreDataBase();
        cdbManager.onUpgrade(new NormalDatabase(db), oldVersion, newVersion);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CoreDatabaseManager cdbManager = CoreData.instance(instanceTag).getCoreDataBase();
        cdbManager.onDowngrade(new NormalDatabase(db), oldVersion, newVersion);
    }

    @Override
    public CoreDatabase getWritableCoreDatabase() {
        return new NormalDatabase(getWritableDatabase());
    }

    @Override
    public CoreDatabase getReadableCoreDatabase() {
        return new NormalDatabase(getReadableDatabase());
    }
}
