package com.coredata.core;

import com.coredata.core.db.Order;

import java.util.List;

/**
 * 结果集，在这里对结果进行筛选，排序等等操作
 *
 * @param <T> 对应的实体类型
 */
public final class ResultSet<T> extends BaseSet<T> {

    private final StringBuilder sqlBuilder;

    ResultSet(CoreDao<T> coreDao) {
        super(coreDao);
        sqlBuilder = new StringBuilder()
                .append("SELECT * FROM ").append(coreDao.getTableName());
    }

    public ResultSet<T> where(String expression) {
        sqlBuilder.append(" WHERE ")
                .append(expression);
        return this;
    }

    public ResultSet<T> and(String expression) {
        sqlBuilder.append(" AND ")
                .append(expression);
        return this;
    }

    public ResultSet<T> or(String expression) {
        sqlBuilder.append(" OR ")
                .append(expression);
        return this;
    }


    public ResultSet<T> groupBy(String expression) {
        sqlBuilder.append(" GROUP BY ")
                .append(expression);
        return this;
    }

    public ResultSet<T> orderBy(String expression) {
        return orderBy(expression, Order.DESC);
    }

    public ResultSet<T> orderBy(String expression, Order order) {
        sqlBuilder.append(" ORDER BY ")
                .append(expression)
                .append(" ")
                .append(order.name());
        return this;
    }

    public ResultSet<T> limit(int size) {
        sqlBuilder.append(" LIMIT ")
                .append(String.valueOf(size));
        return this;
    }

    public ResultSet<T> offset(int offset) {
        sqlBuilder.append(" OFFSET ")
                .append(String.valueOf(offset));
        return this;
    }

    public List<T> result() {
        return getCoreDao().querySqlInternal(sqlBuilder.toString());
    }
}
