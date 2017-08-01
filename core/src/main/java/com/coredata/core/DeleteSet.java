package com.coredata.core;

import com.coredata.core.db.UpdateDeleteSetInterface;
import com.coredata.core.db.UpdateDeleteWhere;

/**
 * Created by wangjinpeng on 2017/6/27.
 */

public class DeleteSet<T> extends BaseSet<T> implements UpdateDeleteSetInterface<T> {

    DeleteSet(CoreDao<T> coreDao) {
        super(coreDao);
        append("DELETE FROM ").append(coreDao.getTableName());
    }

    @Override
    public boolean execute() {
        return getCoreDao().updateDeleteInternal(getSql());
    }

    @Override
    public UpdateDeleteWhere<UpdateDeleteSetInterface<T>, ? extends BaseSet<T>, T> where(String columnName) {
        return new UpdateDeleteWhere<>(this, this, columnName);
    }
}
