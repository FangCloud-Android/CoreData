package com.coredata.core.normal;

import android.database.sqlite.SQLiteStatement;

import com.coredata.core.db.CoreStatement;

/**
 * Created by wangjinpeng on 2017/8/1.
 */

public final class NormalStatement extends CoreStatement {

    private final SQLiteStatement statement;

    public NormalStatement(SQLiteStatement statement) {
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
