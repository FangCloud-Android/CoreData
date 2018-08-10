package com.coredata.core.db;

import com.coredata.utils.SqlUtils;

abstract class BaseWhere<SET extends BaseSet<T>, T> {

    protected final SET set;

    public BaseWhere(SET set, String columnName) {
        this.set = set;
        set.append(" WHERE ")
                .append(columnName);
    }

    public BaseWhere<SET, T> and(String columnName) {
        set.append(" AND ");
        set.append(SqlUtils.formatColumnName(columnName));
        return this;
    }

    public BaseWhere<SET, T> or(String columnName) {
        set.append(" OR ");
        set.append(SqlUtils.formatColumnName(columnName));
        return this;
    }

    /**
     * 相等
     *
     * @param o
     * @return
     */
    public BaseWhere<SET, T> eq(Object o) {
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
    public BaseWhere<SET, T> noteq(Object o) {
        set.append(" <> ")
                .append(SqlUtils.formatValue(o));
        return this;
    }

    public BaseWhere<SET, T> in(Object[] values) {
        StringBuilder append = new StringBuilder();
        boolean isFirst = true;
        for (Object val : values) {
            if (isFirst) {
                isFirst = false;
            } else {
                append.append(",");
            }
            append.append(SqlUtils.formatValue(val));
        }
        set.append(" IN (")
                .append(append.toString())
                .append(")");
        return this;
    }

    public BaseWhere<SET, T> notIn(Object[] values) {
        StringBuilder append = new StringBuilder();
        boolean isFirst = true;
        for (Object val : values) {
            if (isFirst) {
                isFirst = false;
            } else {
                append.append(",");
            }
            append.append(SqlUtils.formatValue(val));
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
    public BaseWhere<SET, T> isNull() {
        set.append(" IS NULL");
        return this;
    }

    /**
     * 不为null
     *
     * @return
     */
    public BaseWhere<SET, T> isNotNull() {
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
    public BaseWhere<SET, T> between(Object lv, Object rv) {
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
    public BaseWhere<SET, T> gt(Object value) {
        set.append(" > ").append(SqlUtils.formatValue(value));
        return this;
    }

    /**
     * 大于等于 (>=)
     *
     * @param value
     * @return
     */
    public BaseWhere<SET, T> gte(Object value) {
        set.append(" >= ").append(SqlUtils.formatValue(value));
        return this;
    }

    /**
     * 小于 (<)
     *
     * @param value
     * @return
     */
    public BaseWhere<SET, T> lt(Object value) {
        set.append(" < ").append(SqlUtils.formatValue(value));
        return this;
    }

    /**
     * 小于等于 (<=)
     *
     * @param value
     * @return
     */
    public BaseWhere<SET, T> lte(Object value) {
        set.append(" <= ").append(SqlUtils.formatValue(value));
        return this;
    }

    /**
     * like
     *
     * @param expression
     * @return
     */
    public BaseWhere<SET, T> like(String expression) {
        set.append(String.format(" LIKE '%s'", expression));
        return this;
    }
}
