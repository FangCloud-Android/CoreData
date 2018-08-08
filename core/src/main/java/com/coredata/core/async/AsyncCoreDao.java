package com.coredata.core.async;

import com.coredata.core.CoreDao;

/**
 * Created by wanpg on 2018/8/9.
 */

public class AsyncCoreDao<T> {

    private final CoreDao<T> realCoreDao;

    public AsyncCoreDao(CoreDao<T> coreDao) {
        this.realCoreDao = coreDao;
    }
}
