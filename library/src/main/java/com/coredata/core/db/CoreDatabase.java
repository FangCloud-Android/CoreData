package com.coredata.core.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

/**
 * Created by wangjinpeng on 2017/7/31.
 */

public class CoreDatabase {

    private net.sqlcipher.database.SQLiteDatabase cipherDb;
    private android.database.sqlite.SQLiteDatabase normalDb;


    public CoreDatabase(net.sqlcipher.database.SQLiteDatabase cipherDb) {
        this.cipherDb = cipherDb;
    }

    public CoreDatabase(android.database.sqlite.SQLiteDatabase normalDb) {
        this.normalDb = normalDb;
    }

    public void beginTransaction() {
        assertDatabase();
        if (normalDb != null) {
            normalDb.beginTransaction();
        } else if (cipherDb != null) {
            cipherDb.beginTransaction();
        }
    }

    public void setTransactionSuccessful() {
        assertDatabase();
        if (normalDb != null) {
            normalDb.setTransactionSuccessful();
        } else if (cipherDb != null) {
            cipherDb.setTransactionSuccessful();
        }
    }

    public void endTransaction() {
        assertDatabase();
        if (normalDb != null) {
            normalDb.endTransaction();
        } else if (cipherDb != null) {
            cipherDb.endTransaction();
        }
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        assertDatabase();
        if (normalDb != null) {
            return normalDb.rawQuery(sql, selectionArgs);
        } else {
            return cipherDb.rawQuery(sql, selectionArgs);
        }
    }

    public CoreStatement compileStatement(String sql) throws SQLException {
        assertDatabase();
        if (normalDb != null) {
            return new CoreStatement(normalDb.compileStatement(sql));
        } else {
            return new CoreStatement(cipherDb.compileStatement(sql));
        }
    }

    public void execSQL(String sql) {
        assertDatabase();
        if (normalDb != null) {
            normalDb.execSQL(sql);
        } else {
            cipherDb.execSQL(sql);
        }
    }

    public long replace(String table, String nullColumnHack, ContentValues initialValues) {

        assertDatabase();
        if (normalDb != null) {
            return normalDb.replace(table, nullColumnHack, initialValues);
        } else {
            return cipherDb.replace(table, nullColumnHack, initialValues);
        }
    }

    private void assertDatabase() {
        if (normalDb == null && cipherDb == null) {
            throw new IllegalStateException("必须配置 normal 和 cipher 两种其一");
        }
    }
}
