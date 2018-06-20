package com.coredata.core;

import android.content.ContentValues;

import com.coredata.core.db.FuncWhere;
import com.coredata.core.result.Result;
import com.coredata.utils.SqlUtils;

import java.util.List;

/**
 * 函数集
 *
 * @param <T> 表相关的实体类型
 */
public class FuncSet<T> extends BaseSet<T> implements Result<ContentValues> {

    private boolean funcAdded = false;
    private boolean hasWhere = false;

    FuncSet(CoreDao<T> coreDao) {
        super(coreDao);
        append("SELECT");
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
        appendFunc(String.format(" max(%s)", SqlUtils.formatColumnName(columnName)), CoreDao.RESULT_MAX);
        return this;
    }

    /**
     * 计算给定列的最小值
     *
     * @param columnName 列名
     * @return
     */
    public FuncSet<T> min(String columnName) {
        appendFunc(String.format(" min(%s)", SqlUtils.formatColumnName(columnName)), CoreDao.RESULT_MIN);
        return this;
    }

    /**
     * 计算给定列的平均值
     *
     * @param columnName 列名
     * @return
     */
    public FuncSet<T> avg(String columnName) {
        appendFunc(String.format(" avg(%s)", SqlUtils.formatColumnName(columnName)), CoreDao.RESULT_AVG);
        return this;
    }

    /**
     * 计算给定列的和
     *
     * @param columnName 列名
     * @return
     */
    public FuncSet<T> sum(String columnName) {
        appendFunc(String.format(" sum(%s)", SqlUtils.formatColumnName(columnName)), CoreDao.RESULT_SUM);
        return this;
    }

    public FuncWhere<FuncSet<T>, T> where(String columnName) {
        hasWhere = true;
        appendFrom();
        return new FuncWhere<>(this, columnName);
    }

    @Override
    public ContentValues result() {
        if (!hasWhere) {
            appendFrom();
        }
        List<ContentValues> contentValuesList = getCoreDao().queryContentValuesInternal(getSql());
        if (contentValuesList != null && !contentValuesList.isEmpty()) {
            return contentValuesList.get(0);
        }
        return new ContentValues();
    }
}





