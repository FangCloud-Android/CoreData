package com.coredata.core;

import com.coredata.core.async.AsyncFuture;
import com.coredata.core.db.IUpdateDelete;
import com.coredata.core.db.UpdateDeleteWhere;

/**
 * 删除操作集
 */

public class DeleteSet<T> extends BaseSet<T> implements IUpdateDelete<T> {

    DeleteSet(CoreDao<T> coreDao) {
        super(coreDao);
        append("DELETE FROM ").append(coreDao.getTableName());
    }

    @Override
    public boolean execute() {
        return getCoreDao().updateDeleteInternal(getSql());
    }

    @Override
    public AsyncFuture<Boolean> executeAsync() {
        return getCoreDao().updateDeleteAsyncInternal(getSql());
    }

    @Override
    public UpdateDeleteWhere<IUpdateDelete<T>, ? extends BaseSet<T>, T> where(String columnName) {
        return new UpdateDeleteWhere<>(this, this, columnName);
    }
}
