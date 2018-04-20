package com.coredata.core.rx;

import com.coredata.core.CoreDao;

public class QueryData {

    private final CoreDao coreDao;

    public QueryData(CoreDao coreDao) {
        this.coreDao = coreDao;
    }

    public CoreDao getCoreDao() {
        return coreDao;
    }
}
