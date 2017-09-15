package com.coredata.core;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.coredata.core.db.Migration;
import com.coredata.core.normal.NormalOpenHelper;
import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.OpenHelperInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjinpeng on 2017/7/31.
 */

public class CoreDatabaseManager {

    private final Map<Class, CoreDao> coreDaoHashMap;

    private OpenHelperInterface openHelper;

    private static final String CIPHER_HELPER_CLASS = "com.coredata.cipher.CipherOpenHelper";

    private Migration migration;

    public CoreDatabaseManager(Context context,
                               String name,
                               int version,
                               HashMap<Class, CoreDao> coreDaoHashMap,
                               String password,
                               Migration migration) {
        this.coreDaoHashMap = coreDaoHashMap;
        this.migration = migration;
        if (TextUtils.isEmpty(password)) {
            openHelper = new NormalOpenHelper(context, name, version);
        } else {
            Class<?> aClass = null;
            try {
                aClass = Class.forName(CIPHER_HELPER_CLASS);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new IllegalStateException("if you want to use sqlite by password, you must dependencies coredata-cipher");
            }
            try {
                openHelper = (OpenHelperInterface) aClass.getConstructor(Context.class, String.class, int.class, String.class)
                        .newInstance(context, name, version, password);
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
            entry.getValue().onDataBaseCreate(cdb);
        }
        Log.d("wanpg", "CoreDataBaseHelper----onCreate");
    }

    public void onUpgrade(CoreDatabase cdb, int oldVersion, int newVersion) {
        if (migration != null) {
            migration.onStart(cdb, oldVersion, newVersion);
        }
        // 先取出老的表结构
        List<String> originTableList = originTableList(cdb);
        // 如果存在执行升级，不存在执行创建
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            CoreDao value = entry.getValue();
            boolean remove = originTableList.remove(value.getTableName());
            if (remove) {
                value.onDataBaseUpgrade(cdb, oldVersion, newVersion);
            } else {
                value.onDataBaseCreate(cdb);
            }
        }
        // 剩下的删除表
        for (String leftTableName : originTableList) {
            cdb.execSQL("DROP TABLE " + leftTableName);
        }
        if (migration != null) {
            migration.onEnd(cdb, oldVersion, newVersion);
        }
        Log.d("wanpg", "CoreDataBaseHelper----onUpgrade");
    }

    public void onDowngrade(CoreDatabase cdb, int oldVersion, int newVersion) {
        // 目前降级操作时删除所有表，并重新创建
        // 先取出老的表结构
        List<String> originTableList = originTableList(cdb);
        // 如果存在执行降级，不存在执行创建
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            CoreDao value = entry.getValue();
            boolean remove = originTableList.remove(value.getTableName());
            if (remove) {
                value.onDataBaseDowngrade(cdb, oldVersion, newVersion);
            } else {
                value.onDataBaseCreate(cdb);
            }
        }
        // 剩下的删除表
        for (String leftTableName : originTableList) {
            cdb.execSQL("DROP TABLE " + leftTableName);
        }
    }

    private List<String> originTableList(CoreDatabase cdb) {
        List<String> nameList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = cdb.rawQuery("select name from sqlite_master where type='table' order by name", null);
            while (cursor.moveToNext()) {
                //遍历出表名
                nameList.add(cursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return nameList;
    }
}
