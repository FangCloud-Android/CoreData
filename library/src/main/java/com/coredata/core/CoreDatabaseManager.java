package com.coredata.core;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.coredata.core.helper.CipherHelper;
import com.coredata.core.db.CoreDatabase;
import com.coredata.core.helper.NormalHelper;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjinpeng on 2017/7/31.
 */

public class CoreDatabaseManager {

    private final Map<Class, CoreDao> coreDaoHashMap;
    private final String password;

    private android.database.sqlite.SQLiteOpenHelper normalHelper;
    private net.sqlcipher.database.SQLiteOpenHelper cipherHelper;

    public CoreDatabaseManager(Context context,
                               String name,
                               int version,
                               HashMap<Class, CoreDao> coreDaoHashMap,
                               String password) {
        this.coreDaoHashMap = coreDaoHashMap;
        this.password = password;
        if (TextUtils.isEmpty(password)) {
            normalHelper = new NormalHelper(context, name, null, version);
        } else {
            SQLiteDatabase.loadLibs(context);
            cipherHelper = new CipherHelper(context, name, null, version);
        }
    }

    public CoreDatabase getWritableDatabase() {
        if (TextUtils.isEmpty(password)) {
            return new CoreDatabase(normalHelper.getWritableDatabase());
        } else {
            return new CoreDatabase(cipherHelper.getWritableDatabase(password));
        }
    }

    public CoreDatabase getReadableDatabase() {
        if (TextUtils.isEmpty(password)) {
            return new CoreDatabase(normalHelper.getReadableDatabase());
        } else {
            return new CoreDatabase(cipherHelper.getReadableDatabase(password));
        }
    }

    public void onCreate(CoreDatabase cdb) {
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            entry.getValue().onDataBaseCreate(cdb);
        }
        Log.d("wanpg", "CoreDataBaseHelper----onCreate");
    }

    public void onUpgrade(CoreDatabase cdb, int oldVersion, int newVersion) {
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
