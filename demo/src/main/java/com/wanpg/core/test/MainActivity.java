package com.wanpg.core.test;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coredata.core.CoreDao;
import com.coredata.core.CoreData;
import com.coredata.core.async.AsyncFuture;
import com.coredata.core.utils.Debugger;
import com.wanpg.core.test.model.Author;
import com.wanpg.core.test.model.Book;
import com.wanpg.core.test.model.Desc;
import com.wanpg.core.test.model.MigrationModel;
import com.wanpg.core.test.model.Tag;
import com.wanpg.coredata.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private int index = 0;

    public void addBookClick(View view) {
        index++;
        Book book = new Book();
        book.id = index;
        book.setName("book_" + index);
        book.desc = new Desc("content" + index, "email" + index);
        book.author = new Author(10000 + index, "author_" + index);
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Tag tag = new Tag(10000000 + index * 1000 + i, "tag_" + index + "_" + i);
            tags.add(tag);
        }
        book.tags = tags;
        Log.d("wanpg", "开始插入一条数据");
        long start = System.currentTimeMillis();
        AsyncFuture<Boolean> booleanAsyncFuture = CoreData.defaultInstance().dao(Book.class).replaceAsync(book);
        booleanAsyncFuture.setCallback(new AsyncFuture.Callback<Boolean>() {
            @Override
            public void response(Boolean aBoolean) {
                Log.d("wanpg", "1条插入完成" + aBoolean);
            }
        });
        Log.d("wanpg", "1条用时" + (System.currentTimeMillis() - start));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "插入数据库第" + index + "本书", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addBookListClick(View view) {
        List<Book> books = new ArrayList<Book>();
        for (int index = 0; index < 10000; index++) {
            Book book = new Book();
            book.id = index;
            book.setName("book_" + index);
            book.desc = new Desc("content" + index, "email" + index);
            book.author = new Author(10000 + index, "author_" + index);
            List<Tag> tags = new ArrayList<Tag>();
            for (int i = 0; i < 2; i++) {
                Tag tag = new Tag(10000000 + index * 1000 + i, "tag_" + index + "_" + i);
                tags.add(tag);
            }
            book.tags = tags;
            books.add(book);
        }
        Log.d("wanpg", "开始插入");
        final long start = System.currentTimeMillis();
        AsyncFuture<Boolean> booleanAsyncFuture = CoreData.defaultInstance().dao(Book.class).replaceAsync(books);
        booleanAsyncFuture.setCallback(new AsyncFuture.Callback<Boolean>() {
            @Override
            public void response(Boolean aBoolean) {
                Log.d("wanpg", "10000条用时真正结束" + (System.currentTimeMillis() - start));
            }
        });
        Log.d("wanpg", "10000条用时" + (System.currentTimeMillis() - start));
    }

    public void queryBookListClick(View view) {
        Log.d("wanpg", "开始读取");
        final long start = System.currentTimeMillis();
        AsyncFuture<List<Book>> listAsyncFuture = CoreData.defaultInstance().dao(Book.class).queryAllAsync();
        listAsyncFuture.setCallback(new AsyncFuture.Callback<List<Book>>() {
            @Override
            public void response(List<Book> books) {
                Log.d("wanpg", "读取" + books.size() + "条书用时" + (System.currentTimeMillis() - start));
            }
        });
    }

    private int queryIndex = 0;

    public void queryBookAndDisplayClick(View view) {
        ContentValues result = CoreData.defaultInstance().dao(Book.class)
                .func()
                .count()
                .result();

        Log.d("wanpg", "所有书的总数" + result.getAsInteger(CoreDao.RESULT_COUNT));

        queryIndex++;
        Log.d("wanpg", "开始读取一本书");
        long start1 = System.currentTimeMillis();
        Book book = CoreData.defaultInstance().dao(Book.class).queryByKey(queryIndex);
        Log.d("wanpg", "读取一本书" + (System.currentTimeMillis() - start1));

        Log.d("wanpg", "开始显示书名和作者名称");
        long start2 = System.currentTimeMillis();
        Author author = book.getAuthor();
        Log.d("wanpg", "作者取出完毕耗时" + (System.currentTimeMillis() - start2));
        if (author != null) {
            TextView textView = (TextView) findViewById(R.id.book_display);
            textView.setText("书名：" + book.getName() + "    作者：" + author.getName());
        }

        Log.d("wanpg", "再尝试取一次吧");
        long start3 = System.currentTimeMillis();
        Author author1 = book.getAuthor();
        Log.d("wanpg", "再尝试取一次吧耗时" + (System.currentTimeMillis() - start3));
    }

    public void deleteAllClick(View view) {
        queryIndex++;
        Log.d("wanpg", "开始删除所有书籍");
        final long start1 = System.currentTimeMillis();
        AsyncFuture<Boolean> booleanAsyncFuture = CoreData.defaultInstance().dao(Book.class).deleteAllAsync();
        booleanAsyncFuture.setCallback(new AsyncFuture.Callback<Boolean>() {
            @Override
            public void response(Boolean aBoolean) {
                Log.d("wanpg", "删除结果" + aBoolean);
                Log.d("wanpg", "删除所有书籍" + (System.currentTimeMillis() - start1));
            }
        });
    }

    public void testRxJava(View view) {
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        Log.d("wanpg", "subscribe 执行线程：" + Thread.currentThread().getName());
                        emitter.onNext("hello world!");
                    }
                })
//                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        Log.d("wanpg", "filter1 执行线程：" + Thread.currentThread().getName());
                        return true;
                    }
                })
                .observeOn(Schedulers.io())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        Log.d("wanpg", "filter2 执行线程：" + Thread.currentThread().getName());
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("wanpg", "onSubscribe 执行线程：" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("wanpg", "onNext 执行线程：" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Disposable disposable;

    public void register(View view) {
        if (disposable != null) {
            return;
        }
        disposable = CoreData.defaultInstance()
                .dao(Book.class)
                .query()
                .observable()
                .subscribe(new Consumer<List<Book>>() {
                    @Override
                    public void accept(List<Book> books) {

                    }
                });
    }

    public void unRegister(View view) {
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = null;
    }

    public void addMigrationModel(View view) {
        List<MigrationModel> modelList = new ArrayList<>();
        modelList.add(MigrationModel.create(1, "name_1", "address_1"));
        modelList.add(MigrationModel.create(2, "name_2", "address_2"));
        modelList.add(MigrationModel.create(3, "name_3", "address_3"));
        modelList.add(MigrationModel.create(4, "name_4", "address_4"));
        modelList.add(MigrationModel.create(5, "name_5", "address_5"));
        modelList.add(MigrationModel.create(6, "name_6", "address_6"));
        CoreDao<MigrationModel> dao = CoreData.defaultInstance().dao(MigrationModel.class);
        dao.replace(modelList);
        Debugger.d("已插入的数据：", dao.queryAll().size());
    }
}
