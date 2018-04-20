package com.wanpg.core.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.coredata.core.CoreData;
import com.wanpg.core.test.model.Book;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class RxJavaActivity extends AppCompatActivity {
    DisposableObserver disposableObserver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposableObserver = CoreData.defaultInstance()
                .dao(Book.class)
                .query()
                .observable()
                .subscribeWith(new DisposableObserver<List<Book>>() {
                    @Override
                    public void onNext(List<Book> books) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })
                /*.subscribe(new Observer<List<Book>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Book> books) {
                        Log.d("wanpg", "当前有多少本书" + (books.size()));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })*/;
        disposableObserver.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void register(View view) {
    }
}
