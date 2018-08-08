package com.coredata.core;

import com.coredata.core.async.AsyncFuture;
import com.coredata.core.db.Update;
import com.coredata.core.db.IUpdateDelete;
import com.coredata.core.db.UpdateDeleteWhere;

/**
 * 更新Set
 */
public class UpdateSet<T> extends BaseSet<T> implements IUpdateDelete<T> {

    UpdateSet(CoreDao<T> coreDao) {
        super(coreDao);
        append("UPDATE ").append(coreDao.getTableName());
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

    public Update<T> set(String columnName) {
        append(" SET ");
        return new Update<>(this, columnName);
    }
}
