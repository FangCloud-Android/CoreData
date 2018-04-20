package com.coredata.core.result;

import com.coredata.core.rx.ResultObservable;

import java.util.List;

public interface QueryResult<T> extends Result<List<T>> {

    /**
     * 返回一个可被观察的对象，支持rxjava的写法
     *
     * @return 可悲观察的对象
     */
    ResultObservable<T> observable();
}
