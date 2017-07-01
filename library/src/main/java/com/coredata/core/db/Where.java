package com.coredata.core.db;

import com.coredata.core.BaseSet;
import com.coredata.utils.SqlUtils;

public class Where<T extends BaseSet> {

    private final T t;

    public Where(T t, String columnName) {
        this.t = t;
        t.append(columnName);
    }

    public T eq(Object o) {
        t.append(" = ")
                .append(SqlUtils.formatValue(o));
        return t;
    }

    public T noteq(Object o) {
        t.append(" <> ")
                .append(SqlUtils.formatValue(o));
        return t;
    }

    public T in(Object... values) {
        StringBuilder append = new StringBuilder();
        boolean isFirst = true;
        for (Object key : values) {
            if (isFirst) {
                isFirst = false;
            } else {
                append.append(",");
            }
            append.append(key);
        }
        t.append(append.toString());
        return t;
    }
}
