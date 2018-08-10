package com.coredata.core.db;

import com.coredata.core.CoreDao;

/**
 * Created by wangjinpeng on 2017/6/27.
 */

public class BaseSet<T> {

    private final CoreDao<T> coreDao;

    private final StringBuilder sqlBuilder;

    public BaseSet(CoreDao<T> coreDao) {
        this.coreDao = coreDao;
        sqlBuilder = new StringBuilder();
    }

    protected CoreDao<T> getCoreDao() {
        return coreDao;
    }

    public final BaseSet<T> append(String e) {
        sqlBuilder.append(e);
        return this;
    }

    protected String getSql() {
        return sqlBuilder.toString();
    }
}
