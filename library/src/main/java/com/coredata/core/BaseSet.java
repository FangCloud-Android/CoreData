package com.coredata.core;

/**
 * Created by wangjinpeng on 2017/6/27.
 */

public abstract class BaseSet<T> {

    private final CoreDao<T> coreDao;

    BaseSet(CoreDao<T> coreDao) {
        this.coreDao = coreDao;
    }

    protected CoreDao<T> getCoreDao() {
        return coreDao;
    }

    public abstract BaseSet<T> append(String e);
}
