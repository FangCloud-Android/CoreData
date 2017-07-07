package com.coredata.core;

import com.coredata.core.db.SetInterface;

/**
 * Created by wangjinpeng on 2017/6/27.
 */

public class BaseSet<T> implements SetInterface<T> {

    private final CoreDao<T> coreDao;

    private final StringBuilder sqlBuilder;

    BaseSet(CoreDao<T> coreDao) {
        this.coreDao = coreDao;
        sqlBuilder = new StringBuilder();
    }

    protected CoreDao<T> getCoreDao() {
        return coreDao;
    }

    @Override
    public final SetInterface<T> append(String e) {
        sqlBuilder.append(e);
        return this;
    }

    protected String getSql() {
        return sqlBuilder.toString();
    }
}
