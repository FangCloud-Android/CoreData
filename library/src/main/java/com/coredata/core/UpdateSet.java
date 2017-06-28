package com.coredata.core;

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

    public UpdateSet<T> set(String expression) {
        sqlBuilder.append(" SET ")
                .append(expression);
        return this;
    }

    public UpdateSet<T> and(String expression) {
        sqlBuilder.append(" AND ")
                .append(expression);
        return this;
    }

    public UpdateSet<T> where(String expression) {
        sqlBuilder.append(" WHERE ")
                .append(expression);
        return this;
    }

    public UpdateSet<T> or(String expression) {
        sqlBuilder.append(" OR ")
                .append(expression);
        return this;
    }

    public boolean execute() {
        return getCoreDao().updateDeleteInternal(sqlBuilder.toString());
    }
}
