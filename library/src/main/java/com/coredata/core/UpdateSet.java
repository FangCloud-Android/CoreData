package com.coredata.core;

import com.coredata.core.db.Update;
import com.coredata.core.db.UpdateDeleteSetInterface;
import com.coredata.core.db.UpdateDeleteWhere;

/**
 * 更新Set
 */
public class UpdateSet<T> extends BaseSet<T> implements UpdateDeleteSetInterface<T> {

    private final StringBuilder sqlBuilder;

    UpdateSet(CoreDao<T> coreDao) {
        super(coreDao);
        sqlBuilder = new StringBuilder()
                .append("UPDATE ").append(coreDao.getTableName());
    }

    @Override
    public UpdateSet<T> append(String e) {
        sqlBuilder.append(e);
        return this;
    }

    @Override
    public boolean execute() {
        return getCoreDao().updateDeleteInternal(sqlBuilder.toString());
    }

    @Override
    public UpdateDeleteWhere<UpdateDeleteSetInterface<T>, ? extends BaseSet<T>, T> where(String columnName) {
        return new UpdateDeleteWhere<>(this, this, columnName);
    }

    public Update<T> set(String columnName) {
        sqlBuilder.append(" SET ");
        return new Update<>(this, columnName);
    }
}
