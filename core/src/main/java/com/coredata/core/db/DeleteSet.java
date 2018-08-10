package com.coredata.core.db;

import com.coredata.core.CoreDao;
import com.coredata.core.async.AsyncCall;
import com.coredata.core.async.AsyncFuture;

/**
 * 删除操作集
 */

public class DeleteSet<T> extends BaseSet<T> implements IUpdateDelete<T> {

    public DeleteSet(CoreDao<T> coreDao) {
        super(coreDao);
        append("DELETE FROM ").append(coreDao.getTableName());
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
}
