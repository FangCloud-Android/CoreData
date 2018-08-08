package com.coredata.core.db;

import com.coredata.core.BaseSet;
import com.coredata.core.async.AsyncFuture;

/**
 * Created by wangjinpeng on 2017/7/4.
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
     */
    AsyncFuture<Boolean> executeAsync();


    UpdateDeleteWhere<IUpdateDelete<T>, ? extends BaseSet<T>, T> where(String columnName);
}
