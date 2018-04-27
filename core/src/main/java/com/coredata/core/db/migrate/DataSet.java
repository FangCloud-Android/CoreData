package com.coredata.core.db.migrate;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.os.Build;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * 数据集合，对应一个表的数据，有表名和数据
 */
public class DataSet implements Iterable<ContentValues> {

    private String tableName;

    private final List<ContentValues> values;

    public DataSet(String tableName, List<ContentValues> values) {
        this.tableName = tableName;
        this.values = values;
    }

    /**
     * 获取数据库表名
     *
     * @return
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 变更数据库表名
     *
     * @param tableName 新的表名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return values == null || values.isEmpty();
    }

    @Override
    public Iterator<ContentValues> iterator() {
        return values.iterator();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void forEach(Consumer<? super ContentValues> action) {
        values.forEach(action);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public Spliterator<ContentValues> spliterator() {
        return values.spliterator();
    }
}
