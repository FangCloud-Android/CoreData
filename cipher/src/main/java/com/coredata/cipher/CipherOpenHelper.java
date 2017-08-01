package com.coredata.cipher;

import android.content.Context;

import com.coredata.core.CoreData;
import com.coredata.core.CoreDatabaseManager;
import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.OpenHelperInterface;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * CoreData绑定sqlcipher的Helper
 *
 * @author wangjinpeng
 */
public class CipherOpenHelper extends SQLiteOpenHelper implements OpenHelperInterface {

    private String password;

    public CipherOpenHelper(Context context, String name, int version, String password) {
        super(context, name, null, version);
        SQLiteDatabase.loadLibs(context);
        this.password = password;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CoreDatabaseManager cdbManager = CoreData.defaultInstance().getCoreDataBase();
        cdbManager.onCreate(new CipherDatabase(db));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CoreDatabaseManager cdbManager = CoreData.defaultInstance().getCoreDataBase();
        cdbManager.onUpgrade(new CipherDatabase(db), oldVersion, newVersion);
    }

    @Override
    public CoreDatabase getWritableCoreDatabase() {
        return new CipherDatabase(getWritableDatabase(password));
    }

    @Override
    public CoreDatabase getReadableCoreDatabase() {
        return new CipherDatabase(getReadableDatabase(password));
    }
}
