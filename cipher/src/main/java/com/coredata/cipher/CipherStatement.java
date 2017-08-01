package com.coredata.cipher;


import com.coredata.core.db.CoreStatement;

import net.sqlcipher.database.SQLiteStatement;

/**
 * Created by wangjinpeng on 2017/8/1.
 */

public final class CipherStatement extends CoreStatement {

    private final SQLiteStatement statement;

    public CipherStatement(SQLiteStatement statement) {
        this.statement = statement;
    }

    @Override
    public int executeUpdateDelete() {
        return statement.executeUpdateDelete();
    }

    @Override
    public long executeInsert() {
        return statement.executeInsert();
    }

    @Override
    public void bindNull(int index) {
        statement.bindNull(index);
    }

    @Override
    public void bindLong(int index, long value) {
        statement.bindLong(index, value);
    }

    @Override
    public void bindDouble(int index, double value) {
        statement.bindDouble(index, value);
    }

    @Override
    public void bindString(int index, String value) {
        statement.bindString(index, value);
    }

    @Override
    public void bindBlob(int index, byte[] value) {
        statement.bindBlob(index, value);
    }
}
