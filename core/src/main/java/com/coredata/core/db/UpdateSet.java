package com.coredata.core.db;

import com.coredata.core.CoreDao;
import com.coredata.core.async.AsyncCall;
import com.coredata.core.async.AsyncFuture;
import com.coredata.utils.SqlUtils;

/**
 * 更新Set
 */
public class UpdateSet<T> extends BaseSet<T> implements IUpdateDelete<T> {

    private boolean hasSet = false;

    public UpdateSet(CoreDao<T> coreDao) {
        super(coreDao);
        append("UPDATE ").append(coreDao.getTableName());
    }

    @Override
    public boolean execute() {
        return getCoreDao().updateDeleteInternal(getSql());
    }

    @Override
    public AsyncFuture<Boolean> executeAsync() {
        return getCoreDao().callAsyncInternal(new AsyncCall<Boolean>() {
            @Override
            public Boolean call() {
                return execute();
            }
        });
    }

    @Override
    public UpdateWhere<IUpdateDelete<T>, ? extends BaseSet<T>, T> where(String columnName) {
        return new UpdateWhere<>(this, this, columnName);
    }

    public UpdateSet<T> set(String columnName, Object value) {
        if (!hasSet) {
            append(" SET ");
        } else {
            append(" AND ");
        }
        hasSet = true;
        append(columnName).append(" = ").append(SqlUtils.formatValue(value));
        return this;
    }
}
