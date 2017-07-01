package com.coredata.core;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CoreData绑定Sqlite3的Helper
 *
 * @author wangjinpeng
 */
public class CoreDataBaseHelper extends SQLiteOpenHelper {

    private HashMap<Class, CoreDao> coreDaoHashMap;

    public CoreDataBaseHelper(Context context, String name, int version, HashMap<Class, CoreDao> coreDaoHashMap) {
        this(context, name, null, version);
        this.coreDaoHashMap = coreDaoHashMap;
    }

    public CoreDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CoreDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            entry.getValue().onDataBaseCreate(db);
        }
        Log.d("wanpg", "CoreDataBaseHelper----onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 先取出老的表结构
        List<String> originTableList = originTableList(db);
        // 如果存在执行升级，不存在执行创建
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            CoreDao value = entry.getValue();
            boolean remove = originTableList.remove(value.getTableName());
            if (remove) {
                value.onDataBaseUpgrade(db, oldVersion, newVersion);
            } else {
                value.onDataBaseCreate(db);
            }
        }
        // 剩下的删除表
        for (String leftTableName : originTableList) {
            db.execSQL("DROP TABLE " + leftTableName);
        }
        Log.d("wanpg", "CoreDataBaseHelper----onUpgrade");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 目前降级操作时删除所有表，并重新创建
        // 先取出老的表结构
        List<String> originTableList = originTableList(db);
        // 如果存在执行降级，不存在执行创建
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            CoreDao value = entry.getValue();
            boolean remove = originTableList.remove(value.getTableName());
            if (remove) {
                value.onDataBaseDowngrade(db, oldVersion, newVersion);
            } else {
                value.onDataBaseCreate(db);
            }
        }
        // 剩下的删除表
        for (String leftTableName : originTableList) {
            db.execSQL("DROP TABLE " + leftTableName);
        }
    }

    private List<String> originTableList(SQLiteDatabase db) {
        List<String> nameList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
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
