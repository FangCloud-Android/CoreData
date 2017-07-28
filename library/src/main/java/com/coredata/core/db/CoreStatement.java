package com.coredata.core.db;

import android.database.sqlite.SQLiteStatement;

/**
 * Created by wangjinpeng on 2017/7/31.
 */

public class CoreStatement {

    private SQLiteStatement normalStatement;
    private net.sqlcipher.database.SQLiteStatement cipherStatement;

    public CoreStatement(SQLiteStatement normalStatement) {
        this.normalStatement = normalStatement;
    }

    public CoreStatement(net.sqlcipher.database.SQLiteStatement cipherStatement) {
        this.cipherStatement = cipherStatement;
    }

    public int executeUpdateDelete() {
        assertDatabase();
        if (normalStatement != null) {
            return normalStatement.executeUpdateDelete();
        } else {
            return cipherStatement.executeUpdateDelete();
        }
    }

    public long executeInsert() {
        assertDatabase();
        if (normalStatement != null) {
            return normalStatement.executeInsert();
        } else {
            return cipherStatement.executeInsert();
        }
    }

    private void assertDatabase() {
        if (normalStatement == null && cipherStatement == null) {
            throw new IllegalStateException("必须配置 normal 和 cipher 两种其一");
        }
    }

    public void bindNull(int index) {
        assertDatabase();
        if (normalStatement != null) {
            normalStatement.bindNull(index);
        } else {
            cipherStatement.bindNull(index);
        }
    }

    public void bindLong(int index, long value) {
        assertDatabase();
        if (normalStatement != null) {
            normalStatement.bindLong(index, value);
        } else {
            cipherStatement.bindLong(index, value);
        }
    }

    public void bindDouble(int index, double value) {
        assertDatabase();
        if (normalStatement != null) {
            normalStatement.bindDouble(index, value);
        } else {
            cipherStatement.bindDouble(index, value);
        }
    }

    public void bindString(int index, String value) {
        assertDatabase();
        if (normalStatement != null) {
            normalStatement.bindString(index, value);
        } else {
            cipherStatement.bindString(index, value);
        }
    }

    public void bindBlob(int index, byte[] value) {
        assertDatabase();
        if (normalStatement != null) {
            normalStatement.bindBlob(index, value);
        } else {
            cipherStatement.bindBlob(index, value);
        }
    }
}
