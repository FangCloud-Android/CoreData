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
import com.wanpg.core.test.model.Author;
import com.wanpg.core.test.model.Book;
import com.wanpg.core.test.model.Desc;
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
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private int index = 0;

    public void addBookClick(View view) {
//        new Thread() {
//            @Override
//            public void run() {
//
//            }
//        }.start();
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
        CoreData.defaultInstance().dao(Book.class).replace(book);
        Log.d("wanpg", "1条用时" + (System.currentTimeMillis() - start));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "插入数据库第" + index + "本书", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addBookListClick(View view) {
        new Thread() {
            @Override
            public void run() {
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
                long start = System.currentTimeMillis();
                CoreData.defaultInstance().dao(Book.class).replace(books);
                Log.d("wanpg", "10000条用时" + (System.currentTimeMillis() - start));
            }
        }.start();
    }

    public void queryBookListClick(View view) {
        new Thread() {
            @Override
            public void run() {
                Log.d("wanpg", "开始读取");
                long start = System.currentTimeMillis();
                List<Book> books1 = CoreData.defaultInstance().dao(Book.class).queryAll();
                Log.d("wanpg", "读取10000条书用时" + (System.currentTimeMillis() - start));
                Log.d("wanpg", "开始读取");
                long start1 = System.currentTimeMillis();
                List<Author> all = CoreData.defaultInstance().dao(Author.class).queryAll();
                Log.d("wanpg", "读取10000条人用时" + (System.currentTimeMillis() - start1));
            }
        }.start();
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
        long start1 = System.currentTimeMillis();
        boolean b = CoreData.defaultInstance().dao(Book.class).deleteAll();
        Log.d("wanpg", "删除结果" + b);
        Log.d("wanpg", "删除所有书籍" + (System.currentTimeMillis() - start1));
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
}
