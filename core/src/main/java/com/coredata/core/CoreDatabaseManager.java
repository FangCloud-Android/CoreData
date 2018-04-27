package com.coredata.core;

import android.content.Context;
import android.text.TextUtils;

import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.OpenHelperInterface;
import com.coredata.core.db.migrate.Migration;
import com.coredata.core.normal.NormalOpenHelper;
import com.coredata.core.utils.Debugger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core 数据库管理类
 */
public final class CoreDatabaseManager {

    private final Map<Class, CoreDao> coreDaoHashMap;

    private OpenHelperInterface openHelper;

    private static final String CIPHER_HELPER_CLASS = "com.coredata.cipher.CipherOpenHelper";

    private String instanceTag;

    private final MigrationWrap migrationWrap;

    public CoreDatabaseManager(Context context,
                               String name,
                               int version,
                               HashMap<Class, CoreDao> coreDaoHashMap,
                               String password,
                               List<Migration> migrations, String tag) {
        this.coreDaoHashMap = coreDaoHashMap;
        migrationWrap = new MigrationWrap(migrations, coreDaoHashMap);
        instanceTag = tag;
        if (TextUtils.isEmpty(password)) {
            openHelper = new NormalOpenHelper(context, name, version, instanceTag);
        } else {
            Class<?> aClass;
            try {
                aClass = Class.forName(CIPHER_HELPER_CLASS);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new IllegalStateException("if you want to use sqlite by password, you must dependencies coredata-cipher");
            }
            try {
                openHelper = (OpenHelperInterface) aClass.getConstructor(
                        Context.class, String.class, int.class, String.class, String.class)
                        .newInstance(context, name, version, password, instanceTag);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("if you want to use sqlite by password, you must dependencies coredata-cipher");
            }
        }
    }

    public CoreDatabase getWritableDatabase() {
        return openHelper.getWritableCoreDatabase();
    }

    public CoreDatabase getReadableDatabase() {
        return openHelper.getReadableCoreDatabase();
    }

    public void onCreate(CoreDatabase cdb) {
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            migrationWrap.onDataBaseCreate(cdb, entry.getValue());
        }
        Debugger.d("CoreDataBaseHelper----onCreate");
    }

    public void onUpgrade(CoreDatabase cdb, int oldVersion, int newVersion) {
        migrationWrap.onUpgrade(cdb, oldVersion, newVersion);
        Debugger.d("CoreDataBaseHelper----onUpgrade");
    }

    public void onDowngrade(CoreDatabase cdb, int oldVersion, int newVersion) {
        migrationWrap.onDowngrade(cdb, oldVersion, newVersion);
        Debugger.d("CoreDataBaseHelper----onDowngrade");
    }
}
