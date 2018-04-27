package com.coredata.core.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.coredata.core.db.CoreDatabase;
import com.coredata.utils.SqlUtils;

import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    public static List<ContentValues> queryAll(CoreDatabase cdb, String table) {
        return queryBySql(cdb, SqlUtils.getSelectAllSql(table));
    }

    public static List<ContentValues> queryBySql(CoreDatabase cdb, String sql) {
        List<ContentValues> contentValuesList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = cdb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, cv);
                contentValuesList.add(cv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return contentValuesList;
    }

    public static void dropTable(CoreDatabase cdb, String table) {
        cdb.execSQL(SqlUtils.getDropTableSql(table));
    }

    public static void deleteAllData(CoreDatabase cdb, String table) {
        cdb.execSQL(SqlUtils.getDeleteAllDataSql(table));
    }

    public static List<String> tableList(CoreDatabase cdb) {
        List<String> nameList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = cdb.rawQuery("select name from sqlite_master where type='table' order by name", null);
            while (cursor.moveToNext()) {
                //遍历出表名
                String name = cursor.getString(0);
                if ("android_metadata".equals(name)) {

                } else {
                    nameList.add(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return nameList;
    }

    /**
     * 关闭游标
     *
     * @param cursor 游标
     */
    public static void closeCursor(Cursor cursor) {
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
