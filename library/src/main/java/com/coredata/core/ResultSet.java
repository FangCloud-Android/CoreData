package com.coredata.core;

import com.coredata.core.db.Order;

import java.util.List;

/**
 * 结果集，在这里对结果进行筛选，排序等等操作
 *
 * @param <T> 对应的实体类型
 */
public final class ResultSet<T> {

    private final CoreDao<T> coreDao;
    private final StringBuilder sqlBuilder;

    ResultSet(CoreDao<T> coreDao) {
        this.coreDao = coreDao;
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

    public List<T> result() {
        return coreDao.querySqlInternal(sqlBuilder.toString());
    }
}
