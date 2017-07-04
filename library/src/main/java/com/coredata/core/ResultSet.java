package com.coredata.core;

import com.coredata.core.db.Order;
import com.coredata.core.db.QueryWhere;

import java.util.List;

/**
 * 结果集，在这里对结果进行筛选，排序等等操作
 *
 * @param <T> 对应的实体类型
 */
public class ResultSet<T> extends BaseSet<T> {

    private final StringBuilder sqlBuilder;

    ResultSet(CoreDao<T> coreDao) {
        super(coreDao);
        sqlBuilder = new StringBuilder()
                .append("SELECT * FROM ").append(coreDao.getTableName());
    }

    @Override
    public ResultSet<T> append(String e) {
        sqlBuilder.append(e);
        return this;
    }

    public List<T> result() {
        return getCoreDao().querySqlInternal(sqlBuilder.toString());
    }

    public QueryWhere<ResultSet<T>, T> where(String columnName) {
        return new QueryWhere<>(this, columnName);
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
}
