package com.coredata.core.rx;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class ResultObservable<T> extends Observable<List<T>> {

    public static <T> Function<Observable<List<T>>, ResultObservable<T>> toFunction() {
        return new Function<Observable<List<T>>, ResultObservable<T>>() {
            @Override
            public ResultObservable<T> apply(Observable<List<T>> listObservable) {
                return new ResultObservable<>(listObservable);
            }
        };
    }

    private final Observable<List<T>> observable;

    public ResultObservable(Observable<List<T>> observable) {
        this.observable = observable;
    }

    @Override
    protected void subscribeActual(final Observer<? super List<T>> observer) {
        observable.subscribe(new ResultObserver<>(observer));
    }

    private static class ResultObserver<T> implements Observer<List<T>> {
        private final Observer<? super List<T>> observer;

        ResultObserver(Observer<? super List<T>> observer) {
            this.observer = observer;
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            observer.onSubscribe(disposable);
        }

        @Override
        public void onNext(List<T> ts) {
            observer.onNext(ts);
        }

        @Override
        public void onError(Throwable throwable) {
            observer.onError(throwable);
        }

        @Override
        public void onComplete() {
            observer.onComplete();
        }
    }
}
