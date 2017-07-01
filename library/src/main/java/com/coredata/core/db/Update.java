package com.coredata.core.db;

import com.coredata.core.BaseSet;
import com.coredata.utils.SqlUtils;

/**
 * Created by wangjinpeng on 2017/7/1.
 */

public class Update<T extends BaseSet> {

    private final T t;

    public Update(T t, String columnName) {
        this.t = t;
        t.append(columnName);
    }

    public T value(Object o) {
        t.append(" = ").append(SqlUtils.formatValue(o));
        return t;
    }
}
