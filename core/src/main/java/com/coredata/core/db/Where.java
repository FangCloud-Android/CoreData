package com.coredata.core.db;

import com.coredata.core.BaseSet;
import com.coredata.utils.SqlUtils;

abstract class Where<SET extends BaseSet<T>, T> {

    protected final SET set;

    public Where(SET set, String columnName) {
        this.set = set;
        set.append(" WHERE ")
                .append(columnName);
    }

    public Where<SET, T> and(String columnName) {
        set.append(" AND ");
        set.append(columnName);
        return this;
    }

    public Where<SET, T> or(String columnName) {
        set.append(" OR ");
        set.append(columnName);
        return this;
    }

    /**
     * 相等
     *
     * @param o
     * @return
     */
    public Where<SET, T> eq(Object o) {
        set.append(" = ")
                .append(SqlUtils.formatValue(o));
        return this;
    }

    /**
     * 不相等
     *
     * @param o
     * @return
     */
    public Where<SET, T> noteq(Object o) {
        set.append(" <> ")
                .append(SqlUtils.formatValue(o));
        return this;
    }

    public Where<SET, T> in(Object[] values) {
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
        set.append(" IN (")
                .append(append.toString())
                .append(")");
        return this;
    }

    public Where<SET, T> notIn(Object[] values) {
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
        set.append(" NOT IN (")
                .append(append.toString())
                .append(")");
        return this;
    }

    /**
     * 为 null
     *
     * @return
     */
    public Where<SET, T> isNull() {
        set.append(" IS NULL");
        return this;
    }

    /**
     * 不为null
     *
     * @return
     */
    public Where<SET, T> isNotNull() {
        set.append(" IS NOT NULL");
        return this;
    }

    /**
     * 区间条件
     *
     * @param lv
     * @param rv
     * @return
     */
    public Where<SET, T> between(Object lv, Object rv) {
        set.append(String.format(
                " BETWEEN %1$s AND %2$S",
                SqlUtils.formatValue(lv),
                SqlUtils.formatValue(rv)));
        return this;
    }

    /**
     * 大于 (>)
     *
     * @param value
     * @return
     */
    public Where<SET, T> gt(Object value) {
        set.append(" > ").append(SqlUtils.formatValue(value));
        return this;
    }

    /**
     * 大于等于 (>=)
     *
     * @param value
     * @return
     */
    public Where<SET, T> gte(Object value) {
        set.append(" >= ").append(SqlUtils.formatValue(value));
        return this;
    }

    /**
     * 小于 (<)
     *
     * @param value
     * @return
     */
    public Where<SET, T> lt(Object value) {
        set.append(" < ").append(SqlUtils.formatValue(value));
        return this;
    }

    /**
     * 小于等于 (<=)
     *
     * @param value
     * @return
     */
    public Where<SET, T> lte(Object value) {
        set.append(" <= ").append(SqlUtils.formatValue(value));
        return this;
    }

    /**
     * like
     *
     * @param expression
     * @return
     */
    public Where<SET, T> like(String expression) {
        set.append(String.format(" <= '%s'", expression));
        return this;
    }
}
