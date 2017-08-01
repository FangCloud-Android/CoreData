package com.coredata.cipher;

import android.content.ContentValues;
import android.database.Cursor;

import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.CoreStatement;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by wangjinpeng on 2017/8/1.
 */

public class CipherDatabase extends CoreDatabase {

    private final SQLiteDatabase db;

    public CipherDatabase(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void beginTransaction() {
        db.beginTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        db.endTransaction();
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    @Override
    public CoreStatement compileStatement(String sql) {
        return new CipherStatement(db.compileStatement(sql));
    }

    @Override
    public void execSQL(String sql) {
        db.execSQL(sql);
    }

    @Override
    public long replace(String table, String nullColumnHack, ContentValues initialValues) {
        return db.replace(table, nullColumnHack, initialValues);
    }
}
