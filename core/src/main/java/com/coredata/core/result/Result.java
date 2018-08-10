package com.coredata.core.result;

import com.coredata.core.async.AsyncFuture;

public interface Result<T> {

    T result();

    AsyncFuture<T> resultAsync();
}
