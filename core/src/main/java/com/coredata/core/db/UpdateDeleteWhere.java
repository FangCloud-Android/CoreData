package com.coredata.core.db;

import com.coredata.core.BaseSet;

/**
 * Created by wangjinpeng on 2017/7/4.
 */

public class UpdateDeleteWhere<SI extends UpdateDeleteSetInterface<T>, SET extends BaseSet<T>, T> extends Where<SET, T> {

    private UpdateDeleteSetInterface setInterface;

    public UpdateDeleteWhere(UpdateDeleteSetInterface<T> setInterface, SET set, String columnName) {
        super(set, columnName);
        this.setInterface = setInterface;
    }

    public boolean execute() {
        return setInterface.execute();
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> and(String columnName) {
        super.and(columnName);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> or(String columnName) {
        super.or(columnName);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> eq(Object o) {
        super.eq(o);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> noteq(Object o) {
        super.noteq(o);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> in(Object[] values) {
        super.in(values);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> notIn(Object[] values) {
        super.notIn(values);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> isNull() {
        super.isNull();
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> isNotNull() {
        super.isNotNull();
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> between(Object lv, Object rv) {
        super.between(lv, rv);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> gt(Object value) {
        super.gt(value);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> gte(Object value) {
        super.gte(value);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> lt(Object value) {
        super.lt(value);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> lte(Object value) {
        super.lte(value);
        return this;
    }

    @Override
    public UpdateDeleteWhere<SI, SET, T> like(String expression) {
        super.like(expression);
        return this;
    }
}
