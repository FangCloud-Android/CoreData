package com.coredata.core.db;

import com.coredata.core.BaseSet;
import com.coredata.core.UpdateSet;
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
        t.append(" IN (")
                .append(append.toString())
                .append(")");
        return t;
    }

    public T notIn(Object... values) {
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
        t.append(" NOT IN (")
                .append(append.toString())
                .append(")");
        return t;
    }

    /**
     * 为 null
     *
     * @return
     */
    public T isNull() {
        t.append(" IS NULL");
        return t;
    }

    /**
     * 不为null
     *
     * @return
     */
    public T isNotNull() {
        t.append(" IS NOT NULL");
        return t;
    }

    /**
     * 区间条件
     *
     * @param lv
     * @param rv
     * @return
     */
    public T between(Object lv, Object rv) {
        t.append(String.format(
                " BETWEEN %1$s AND %2$S",
                SqlUtils.formatValue(lv),
                SqlUtils.formatValue(rv)));
        return t;
    }

    /**
     * 大于 (>)
     *
     * @param value
     * @return
     */
    public T gt(Object value) {
        t.append(" > ").append(SqlUtils.formatValue(value));
        return t;
    }

    /**
     * 大于等于 (>=)
     *
     * @param value
     * @return
     */
    public T gte(Object value) {
        t.append(" >= ").append(SqlUtils.formatValue(value));
        return t;
    }

    /**
     * 小于 (<)
     *
     * @param value
     * @return
     */
    public T lt(Object value) {
        t.append(" < ").append(SqlUtils.formatValue(value));
        return t;
    }

    /**
     * 小于等于 (<=)
     *
     * @param value
     * @return
     */
    public T lte(Object value) {
        t.append(" <= ").append(SqlUtils.formatValue(value));
        return t;
    }

    /**
     * like
     *
     * @param expression
     * @return
     */
    public T like(String expression) {
        t.append(String.format(" <= '%s'", expression));
        return t;
    }
}
