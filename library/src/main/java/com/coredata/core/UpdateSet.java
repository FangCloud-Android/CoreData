package com.coredata.core;

import com.coredata.core.db.Update;
import com.coredata.core.db.UpdateDeleteSetInterface;
import com.coredata.core.db.UpdateDeleteWhere;

/**
 * 更新Set
 */
public class UpdateSet<T> extends BaseSet<T> implements UpdateDeleteSetInterface<T> {

    UpdateSet(CoreDao<T> coreDao) {
        super(coreDao);
        append("UPDATE ").append(coreDao.getTableName());
    }

    @Override
    public boolean execute() {
        return getCoreDao().updateDeleteInternal(getSql());
    }

    @Override
    public UpdateDeleteWhere<UpdateDeleteSetInterface<T>, ? extends BaseSet<T>, T> where(String columnName) {
        return new UpdateDeleteWhere<>(this, this, columnName);
    }

    public Update<T> set(String columnName) {
        append(" SET ");
        return new Update<>(this, columnName);
    }
}
