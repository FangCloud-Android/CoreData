package com.coredata.core.db;

/**
 * Created by wangjinpeng on 2017/7/31.
 */

public abstract class CoreStatement {

    public abstract int executeUpdateDelete();

    public abstract long executeInsert();

    public abstract void bindNull(int index);

    public abstract void bindLong(int index, long value);

    public abstract void bindDouble(int index, double value);

    public abstract void bindString(int index, String value);

    public abstract void bindBlob(int index, byte[] value);
}
