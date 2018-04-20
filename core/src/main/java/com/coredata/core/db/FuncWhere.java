package com.coredata.core.db;

import android.content.ContentValues;

import com.coredata.core.FuncSet;
import com.coredata.core.result.Result;

public class FuncWhere<SET extends FuncSet<T>, T> extends Where<SET, T> implements Result<ContentValues> {

    public FuncWhere(SET set, String columnName) {
        super(set, columnName);
    }

    /**
     * 获取结果
     *
     * @see FuncSet#result()
     */
    @Override
    public ContentValues result() {
        return set.result();
    }

    @Override
    public FuncWhere<SET, T> and(String columnName) {
        super.and(columnName);
        return this;
    }

    @Override
    public FuncWhere<SET, T> or(String columnName) {
        super.or(columnName);
        return this;
    }

    @Override
    public FuncWhere<SET, T> eq(Object o) {
        super.eq(o);
        return this;
    }

    @Override
    public FuncWhere<SET, T> noteq(Object o) {
        super.noteq(o);
        return this;
    }

    @Override
    public FuncWhere<SET, T> in(Object[] values) {
        super.in(values);
        return this;
    }

    @Override
    public FuncWhere<SET, T> notIn(Object[] values) {
        super.notIn(values);
        return this;
    }

    @Override
    public FuncWhere<SET, T> isNull() {
        super.isNull();
        return this;
    }

    @Override
    public FuncWhere<SET, T> isNotNull() {
        super.isNotNull();
        return this;
    }

    @Override
    public FuncWhere<SET, T> between(Object lv, Object rv) {
        super.between(lv, rv);
        return this;
    }

    @Override
    public FuncWhere<SET, T> gt(Object value) {
        super.gt(value);
        return this;
    }

    @Override
    public FuncWhere<SET, T> gte(Object value) {
        super.gte(value);
        return this;
    }

    @Override
    public FuncWhere<SET, T> lt(Object value) {
        super.lt(value);
        return this;
    }

    @Override
    public FuncWhere<SET, T> lte(Object value) {
        super.lte(value);
        return this;
    }

    @Override
    public FuncWhere<SET, T> like(String expression) {
        super.like(expression);
        return this;
    }
}
