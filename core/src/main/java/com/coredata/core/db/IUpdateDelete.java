package com.coredata.core.db;

import com.coredata.core.async.AsyncFuture;

/**
 * 更新删除操作的接口
 */

public interface IUpdateDelete<T> {

    /**
     * 同步执行
     *
     * @return
     */
    boolean execute();

    /**
     * 异步执行
     *
     * @return
     */
    AsyncFuture<Boolean> executeAsync();


    UpdateWhere<IUpdateDelete<T>, ? extends BaseSet<T>, T> where(String columnName);
}
