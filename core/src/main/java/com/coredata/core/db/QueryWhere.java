package com.coredata.core.db;

import com.coredata.core.ResultSet;

import java.util.List;

/**
 * Created by wangjinpeng on 2017/7/4.
 */

public class QueryWhere<SET extends ResultSet<T>, T> extends Where<SET, T> {

    public QueryWhere(SET set, String columnName) {
        super(set, columnName);
    }

    public List<T> result() {
        return set.result();
    }

    public ResultSet<T> groupBy(String expression) {
        return set.groupBy(expression);
    }

    public ResultSet<T> orderBy(String expression) {
        return set.orderBy(expression);
    }

    public ResultSet<T> orderBy(String expression, Order order) {
        return set.orderBy(expression, order);
    }

    public ResultSet<T> limit(int size) {
        return set.limit(size);
    }

    @Override
    public QueryWhere<SET, T> and(String columnName) {
        super.and(columnName);
        return this;
    }

    @Override
    public QueryWhere<SET, T> or(String columnName) {
        super.or(columnName);
        return this;
    }

    @Override
    public QueryWhere<SET, T> eq(Object o) {
        super.eq(o);
        return this;
    }

    @Override
    public QueryWhere<SET, T> noteq(Object o) {
        super.noteq(o);
        return this;
    }

    @Override
    public QueryWhere<SET, T> in(Object[] values) {
        super.in(values);
        return this;
    }

    @Override
    public QueryWhere<SET, T> notIn(Object[] values) {
        super.notIn(values);
        return this;
    }

    @Override
    public QueryWhere<SET, T> isNull() {
        super.isNull();
        return this;
    }

    @Override
    public QueryWhere<SET, T> isNotNull() {
        super.isNotNull();
        return this;
    }

    @Override
    public QueryWhere<SET, T> between(Object lv, Object rv) {
        super.between(lv, rv);
        return this;
    }

    @Override
    public QueryWhere<SET, T> gt(Object value) {
        super.gt(value);
        return this;
    }

    @Override
    public QueryWhere<SET, T> gte(Object value) {
        super.gte(value);
        return this;
    }

    @Override
    public QueryWhere<SET, T> lt(Object value) {
        super.lt(value);
        return this;
    }

    @Override
    public QueryWhere<SET, T> lte(Object value) {
        super.lte(value);
        return this;
    }

    @Override
    public QueryWhere<SET, T> like(String expression) {
        super.like(expression);
        return this;
    }
}
