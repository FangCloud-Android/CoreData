package com.coredata.core;

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

    public DeleteSet<T> where(String expression) {
        sqlBuilder.append(" WHERE ")
                .append(expression);
        return this;
    }

    public DeleteSet<T> and(String expression) {
        sqlBuilder.append(" AND ")
                .append(expression);
        return this;
    }

    public DeleteSet<T> or(String expression) {
        sqlBuilder.append(" OR ")
                .append(expression);
        return this;
    }

    public boolean execute() {
        return getCoreDao().updateDeleteInternal(sqlBuilder.toString());
    }
}
