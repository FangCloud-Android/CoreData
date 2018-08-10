package com.coredata.core.db;

import com.coredata.core.CoreDao;
import com.coredata.core.async.AsyncCall;
import com.coredata.core.async.AsyncFuture;
import com.coredata.core.result.QueryResult;
import com.coredata.core.rx.ResultObservable;

import java.util.List;

/**
 * 查询结果集，在这里对结果进行筛选，排序等等操作
 *
 * @param <T> 对应的实体类型
 */
public class QuerySet<T> extends BaseSet<T> implements QueryResult<T> {

    public QuerySet(CoreDao<T> coreDao) {
        super(coreDao);
        append("SELECT * FROM ").append(coreDao.getTableName());
    }

    public QueryWhere<QuerySet<T>, T> where(String columnName) {
        return new QueryWhere<>(this, columnName);
    }

    public QuerySet<T> groupBy(String expression) {
        append(" GROUP BY ")
                .append(expression);
        return this;
    }

    public QuerySet<T> orderBy(String expression) {
        return orderBy(expression, Order.DESC);
    }

    public QuerySet<T> orderBy(String expression, Order order) {
        append(" ORDER BY ")
                .append(expression)
                .append(" ")
                .append(order.name());
        return this;
    }

    public QuerySet<T> limit(int size) {
        append(" LIMIT ")
                .append(String.valueOf(size));
        return this;
    }

    public QuerySet<T> offset(int offset) {
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

    @Override
    public AsyncFuture<List<T>> resultAsync() {
        return getCoreDao().callAsyncInternal(new AsyncCall<List<T>>() {
            @Override
            public List<T> call() {
                return result();
            }
        });
    }
}
