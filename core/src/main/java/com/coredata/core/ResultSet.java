package com.coredata.core;

import com.coredata.core.db.Order;
import com.coredata.core.db.QueryWhere;
import com.coredata.core.result.QueryResult;
import com.coredata.core.rx.ResultObservable;

import java.util.List;

/**
 * 结果集，在这里对结果进行筛选，排序等等操作
 *
 * @param <T> 对应的实体类型
 */
public class ResultSet<T> extends BaseSet<T> implements QueryResult<T> {

    ResultSet(CoreDao<T> coreDao) {
        super(coreDao);
        append("SELECT * FROM ").append(coreDao.getTableName());
    }

    public QueryWhere<ResultSet<T>, T> where(String columnName) {
        return new QueryWhere<>(this, columnName);
    }

    public ResultSet<T> groupBy(String expression) {
        append(" GROUP BY ")
                .append(expression);
        return this;
    }

    public ResultSet<T> orderBy(String expression) {
        return orderBy(expression, Order.DESC);
    }

    public ResultSet<T> orderBy(String expression, Order order) {
        append(" ORDER BY ")
                .append(expression)
                .append(" ")
                .append(order.name());
        return this;
    }

    public ResultSet<T> limit(int size) {
        append(" LIMIT ")
                .append(String.valueOf(size));
        return this;
    }

    public ResultSet<T> offset(int offset) {
        append(" OFFSET ")
                .append(String.valueOf(offset));
        return this;
    }

    @Override
    public ResultObservable<T> observable() {
        return getCoreDao().observable(this);
    }

    @Override
    public List<T> result() {
        return getCoreDao().querySqlInternal(getSql());
    }
}
