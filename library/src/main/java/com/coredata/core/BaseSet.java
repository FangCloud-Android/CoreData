package com.coredata.core;

import com.coredata.core.db.SetInterface;

/**
 * Created by wangjinpeng on 2017/6/27.
 */

public class BaseSet<T> implements SetInterface<T>{

    private final CoreDao<T> coreDao;

    BaseSet(CoreDao<T> coreDao) {
        this.coreDao = coreDao;
    }

    protected CoreDao<T> getCoreDao() {
        return coreDao;
    }

    @Override
    public SetInterface<T> append(String e) {
        return null;
    }
}
