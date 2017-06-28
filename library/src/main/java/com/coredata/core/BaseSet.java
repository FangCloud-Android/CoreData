package com.coredata.core;

/**
 * Created by wangjinpeng on 2017/6/27.
 */

public class BaseSet<T> {

    private final CoreDao<T> coreDao;

    BaseSet(CoreDao<T> coreDao) {
        this.coreDao = coreDao;
    }

    protected CoreDao<T> getCoreDao() {
        return coreDao;
    }
}
