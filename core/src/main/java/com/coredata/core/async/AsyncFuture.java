package com.coredata.core.async;

/**
 *
 */
public final class AsyncFuture<T> {

    public interface Callback<T> {
        void response(T t);
    }

    private Callback<T> callback;

    public void setCallback(Callback<T> callback) {
        this.callback = callback;
    }

    public Callback<T> getCallback() {
        return callback;
    }
}
