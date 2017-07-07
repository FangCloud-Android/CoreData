package com.coredata.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.coredata.core.db.FuncWhere;

/**
 * 函数集
 *
 * @param <T> 表相关的实体类型
 */
public class FuncSet<T> extends BaseSet<T> {

    private boolean funcAdded = false;
    private boolean hasWhere = false;

    FuncSet(CoreDao<T> coreDao) {
        super(coreDao);
        append("SELECT");
    }

    /**
     * 获取结果
     *
     * @return 返回给定类型的结果
     */
    public ContentValues result() {
        if (!hasWhere) {
            appendFrom();
        }
        ContentValues contentValues = new ContentValues();
        Cursor cursor = null;
        try {
            cursor = getCoreDao().querySqlCursor(getSql());
            if (cursor.moveToNext()) {
                DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getCoreDao().closeCursor(cursor);
        }
        return contentValues;
    }

    private void appendFunc(String funcStr, String asName) {
        if (funcAdded) {
            append(", ");
        }
        funcAdded = true;
        append(funcStr).append(" AS ").append(asName);
    }

    private void appendFrom() {
        append(" from ").append(getCoreDao().getTableName());
    }

    /**
     * 计算满足条件的数量
     *
     * @return
     */
    public FuncSet<T> count() {
        appendFunc(" count(*)", CoreDao.RESULT_COUNT);
        return this;
    }

    /**
     * 计算给定列的最大值
     *
     * @param columnName 列名
     * @return
     */
    public FuncSet<T> max(String columnName) {
        appendFunc(String.format(" max(%s)", columnName), CoreDao.RESULT_MAX);
        return this;
    }

    /**
     * 计算给定列的最小值
     *
     * @param columnName 列名
     * @return
     */
    public FuncSet<T> min(String columnName) {
        appendFunc(String.format(" min(%s)", columnName), CoreDao.RESULT_MIN);
        return this;
    }

    /**
     * 计算给定列的平均值
     *
     * @param columnName 列名
     * @return
     */
    public FuncSet<T> avg(String columnName) {
        appendFunc(String.format(" avg(%s)", columnName), CoreDao.RESULT_AVG);
        return this;
    }

    /**
     * 计算给定列的和
     *
     * @param columnName 列名
     * @return
     */
    public FuncSet<T> sum(String columnName) {
        appendFunc(String.format(" sum(%s)", columnName), CoreDao.RESULT_SUM);
        return this;
    }

    public FuncWhere<FuncSet<T>, T> where(String columnName) {
        hasWhere = true;
        appendFrom();
        return new FuncWhere<>(this, columnName);
    }
}





