package com.coredata.core;

import com.coredata.core.db.Update;
import com.coredata.core.db.Where;

/**
 * 更新Set
 */
public class UpdateSet<T> extends BaseSet<T> {

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

    public Update<UpdateSet<T>> set(String columnName) {
        sqlBuilder.append(" SET ");
        return new Update<>(this, columnName);
    }

    public Update<UpdateSet<T>> andSet(String columnName) {
        sqlBuilder.append(" AND ");
        return new Update<>(this, columnName);
    }

    public Where<UpdateSet<T>> where(String columnName) {
        sqlBuilder.append(" WHERE ");
        return new Where<>(this, columnName);
    }

    public Where<UpdateSet<T>> andWhere(String columnName) {
        sqlBuilder.append(" AND ");
        return new Where<>(this, columnName);
    }

    public Where<UpdateSet<T>> orWhere(String columnName) {
        sqlBuilder.append(" OR ");
        return new Where<>(this, columnName);
    }

    public boolean execute() {
        return getCoreDao().updateDeleteInternal(sqlBuilder.toString());
    }
}
