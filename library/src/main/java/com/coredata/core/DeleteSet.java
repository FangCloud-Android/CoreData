package com.coredata.core;

import com.coredata.core.db.Where;

/**
 * Created by wangjinpeng on 2017/6/27.
 */

public class DeleteSet<T> extends BaseSet<T> {

    private final StringBuilder sqlBuilder;

    DeleteSet(CoreDao<T> coreDao) {
        super(coreDao);
        sqlBuilder = new StringBuilder()
                .append("DELETE FROM ").append(coreDao.getTableName());
    }

    @Override
    public DeleteSet<T> append(String e) {
        sqlBuilder.append(e);
        return this;
    }

    public Where<DeleteSet<T>> where(String columnName) {
        sqlBuilder.append(" WHERE ");
        return new Where<>(this, columnName);
    }

    public Where<DeleteSet<T>> andWhere(String columnName) {
        sqlBuilder.append(" AND ");
        return new Where<>(this, columnName);
    }

    public Where<DeleteSet<T>> orWhere(String columnName) {
        sqlBuilder.append(" OR ");
        return new Where<>(this, columnName);
    }

    public boolean execute() {
        return getCoreDao().updateDeleteInternal(sqlBuilder.toString());
    }
}
