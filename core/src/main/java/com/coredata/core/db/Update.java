package com.coredata.core.db;

import com.coredata.core.BaseSet;
import com.coredata.core.UpdateSet;
import com.coredata.utils.SqlUtils;

/**
 * Created by wangjinpeng on 2017/7/1.
 */

public class Update<T> {

    private final UpdateSet<T> set;

    public Update(UpdateSet<T> set, String columnName) {
        this.set = set;
        set.append(columnName);
    }

    public Update<T> value(Object o) {
        set.append(" = ").append(SqlUtils.formatValue(o));
        return this;
    }

    public Update<T> and(Object o) {
        set.append(" AND ");
        return this;
    }

    public UpdateDeleteWhere<UpdateDeleteSetInterface<T>, ? extends BaseSet<T>, T> where(String columnName) {
        return set.where(columnName);
    }

    public boolean execute() {
        return set.execute();
    }
}
