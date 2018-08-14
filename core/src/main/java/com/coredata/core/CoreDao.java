package com.coredata.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.coredata.core.async.AsyncCall;
import com.coredata.core.async.AsyncFuture;
import com.coredata.core.async.AsyncThreadFactory;
import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.CoreStatement;
import com.coredata.core.db.DeleteSet;
import com.coredata.core.db.FuncSet;
import com.coredata.core.db.QuerySet;
import com.coredata.core.db.UpdateSet;
import com.coredata.core.rx.QueryData;
import com.coredata.core.rx.ResultObservable;
import com.coredata.core.rx.ResultQuery;
import com.coredata.core.utils.DBUtils;
import com.coredata.core.utils.Debugger;
import com.coredata.db.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

/**
 * Dao，实体都会拥有一个Dao实例，可进行增删改查
 *
 * @param <T> Dao对应的实体类
 */
public abstract class CoreDao<T> {

    public static final String RESULT_COUNT = "result_count";
    public static final String RESULT_MAX = "result_max";
    public static final String RESULT_MIN = "result_min";
    public static final String RESULT_AVG = "result_avg";
    public static final String RESULT_SUM = "result_sum";

    private CoreData cdInstance;
    public static ExecutorService executor = Executors.newFixedThreadPool(2, new AsyncThreadFactory());

    /**
     * 数据库创建
     *
     * @param coreData CoreData实例
     */
    protected void onCreate(CoreData coreData) {
        this.cdInstance = coreData;
    }

    CoreData getCoreData() {
        return cdInstance;
    }

    /**
     * 获取表名
     *
     * @return
     */
    public abstract String getTableName();

    /**
     * 获取主键的名字
     *
     * @return
     */
    public abstract String getPrimaryKeyName();

    /**
     * 获取table所有的fields
     *
     * @return
     */
    public abstract List<Property> getTableProperties();

    /**
     * 获取创建表的sql
     *
     * @return
     */
    protected abstract String getCreateTableSql();

    /**
     * 获取插入语句
     *
     * @return 返回插入数据库语句，例如"INSERT OR REPLACE INTO `Book`(`id`,`name`,`tags`,`author_id`,`desc_content`,`desc_email`) VALUES (?,?,?,?,?,?)"
     */
    protected abstract String getInsertSql();

    /**
     * 绑定数据到statement
     *
     * @param statement
     * @param t
     */
    protected abstract void bindStatement(CoreStatement statement, T t);

    /**
     * 内部替换
     *
     * @param tCollection
     * @param db
     * @return
     */
    protected abstract boolean replaceInternal(Collection<T> tCollection, CoreDatabase db);

    /**
     * 游标绑定
     *
     * @param cursor
     * @return
     */
    protected abstract List<T> bindCursor(Cursor cursor);

    /**
     * 单条数据插入 内部使用
     *
     * @param t   实体对象
     * @param cdb SQLiteDatabase对象
     * @return 是否插入成功
     */
    protected boolean replace(T t, CoreDatabase cdb) {
        // 查找出 所有关联的对象对应的dao，以及所对应的dao
        ArrayList<T> tList = new ArrayList<>();
        tList.add(t);
        return replace(tList, cdb);
    }

    /**
     * 内部方法
     *
     * @param tCollection 实体集合
     * @param cdb         SQLiteDatabase对象
     * @return 是否插入成功
     */
    public boolean replace(Collection<T> tCollection, CoreDatabase cdb) {
        return replaceInternal(tCollection, cdb);
    }

    /**
     * 绑定数据并提交插入, 内部使用
     *
     * @param tList 实例List
     * @param cdb   SQLiteDatabase对象
     * @return 是否插入成功
     */
    protected boolean executeInsert(List<T> tList, CoreDatabase cdb) {
        String insertSql = getInsertSql();
        Debugger.d("CoreDao--executeInsert--sql:", insertSql, "--data size:", tList == null ? 0 : tList.size());
        CoreStatement cs = cdb.compileStatement(insertSql);
        for (T t : tList) {
            bindStatement(cs, t);
            cs.executeInsert();
        }
        return true;
    }

    /**
     * 单条数据插入
     *
     * @param t 实体对象
     * @return 是否插入成功
     */
    public boolean replace(T t) {
        if (t == null) {
            return false;
        }
        try {
            CoreDatabase cdb = cdInstance.getCoreDataBase().getWritableDatabase();
            cdb.beginTransaction();
            replace(t, cdb);
            cdb.setTransactionSuccessful();
            cdb.endTransaction();
            return true;
        } finally {
            sendTrigger(new QueryData(this));
        }
    }

    /**
     * 异步单条数据插入
     *
     * @param t 实体对象
     * @return 同步结果回调
     */
    public AsyncFuture<Boolean> replaceAsync(final T t) {
        return callAsyncInternal(new AsyncCall<Boolean>() {
            @Override
            public Boolean call() {
                return replace(t);
            }
        });
    }

    /**
     * 插入集合数据
     *
     * @param tCollection 实体集合
     * @return 是否插入成功
     */
    public boolean replace(Collection<T> tCollection) {
        try {
            CoreDatabase cdb = cdInstance.getCoreDataBase().getWritableDatabase();
            cdb.beginTransaction();
            replace(tCollection, cdb);
            cdb.setTransactionSuccessful();
            cdb.endTransaction();
            return true;
        } finally {
            sendTrigger(new QueryData(this));
        }
    }

    /**
     * 异步插入一组数据
     *
     * @param tCollection
     * @return
     */
    public AsyncFuture<Boolean> replaceAsync(final Collection<T> tCollection) {
        return callAsyncInternal(new AsyncCall<Boolean>() {
            @Override
            public Boolean call() {
                return replace(tCollection);
            }
        });
    }

    /**
     * 查询所有数据
     *
     * @return 实体对象List
     */
    public List<T> queryAll() {
        return query().result();
    }

    /**
     * 异步查询所有数据
     *
     * @return
     */
    public AsyncFuture<List<T>> queryAllAsync() {
        return callAsyncInternal(new AsyncCall<List<T>>() {
            @Override
            public List<T> call() {
                return queryAll();
            }
        });
    }

    /**
     * 根据主键查询数据
     *
     * @param key 主键value
     * @return 实体对象，可能为null
     */
    public T queryByKey(Object key) {
        List<T> tList = query()
                .where(getPrimaryKeyName()).eq(key)
                .result();
        if (tList != null && !tList.isEmpty()) {
            return tList.get(0);
        }
        return null;
    }

    /**
     * 异步查询主键为key的数据
     *
     * @param key
     * @return
     */
    public AsyncFuture<T> queryByKeyAsync(final Object key) {
        return callAsyncInternal(new AsyncCall<T>() {
            @Override
            public T call() {
                return queryByKey(key);
            }
        });
    }

    /**
     * 根据给定的主键列表查询数据
     *
     * @param keys 主键values
     * @return 实体对象List
     */
    public List<T> queryByKeys(Object[] keys) {
        if (keys == null || keys.length <= 0) {
            return new ArrayList<>();
        }
        return query()
                .where(getPrimaryKeyName()).in(keys)
                .result();
    }

    /**
     * 异步查询相关主键keys的数据
     *
     * @param keys
     * @return
     */
    public AsyncFuture<List<T>> queryByKeysAsync(final Object[] keys) {
        return callAsyncInternal(new AsyncCall<List<T>>() {
            @Override
            public List<T> call() {
                return queryByKeys(keys);
            }
        });
    }

    /**
     * 查询，生成一个查询用的结构处理集
     *
     * @return 创建一个结果处理集合
     */
    public QuerySet<T> query() {
        return new QuerySet<>(this);
    }

    /**
     * 根据给定的条件进行查询
     *
     * @param sql sql语句
     * @return 实体对象List
     */
    public Cursor querySqlCursor(String sql) {
        Debugger.d("CoreDao--querySqlCursor--sql:" + sql);
        CoreDatabase cdb;
        try {
            cdb = cdInstance.getCoreDataBase().getReadableDatabase();
            return cdb.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除全部
     *
     * @return 是否删除成功
     */
    public boolean deleteAll() {
        return delete().execute();
    }

    /**
     * 异步删除所有数据
     *
     * @return
     */
    public AsyncFuture<Boolean> deleteAllAsync() {
        return callAsyncInternal(new AsyncCall<Boolean>() {
            @Override
            public Boolean call() {
                return deleteAll();
            }
        });
    }

    /**
     * 根据给定主键删除
     *
     * @param key 主键value
     * @return 是否删除成功
     */
    public boolean deleteByKey(Object key) {
        return delete()
                .where(getPrimaryKeyName()).eq(key)
                .execute();
    }

    public AsyncFuture<Boolean> deleteByKeyAsync(final Object key) {
        return callAsyncInternal(new AsyncCall<Boolean>() {
            @Override
            public Boolean call() {
                return deleteByKey(key);
            }
        });
    }

    /**
     * 根据给定主键列表删除
     *
     * @param keys 主键value列表
     * @return 是否删除成功
     */
    public boolean deleteByKeys(Object[] keys) {
        if (keys == null || keys.length <= 0) {
            return false;
        }
        return delete()
                .where(getPrimaryKeyName()).in(keys)
                .execute();
    }

    public AsyncFuture<Boolean> deleteByKeysAsync(final Object[] keys) {
        return callAsyncInternal(new AsyncCall<Boolean>() {
            @Override
            public Boolean call() {
                return deleteByKeys(keys);
            }
        });
    }

    /**
     * 返回一个删除的操作集合
     *
     * @return 删除操作集合，这里可以写入where条件
     */
    public DeleteSet<T> delete() {
        return new DeleteSet<>(this);
    }

    /**
     * 返回一个更新的操作集合
     *
     * @return 更新操作集合，在这里可以做set和where的操作
     */
    public UpdateSet<T> update() {
        return new UpdateSet<>(this);
    }

    /**
     * 函数功能
     *
     * @return 函数操作集
     */
    public FuncSet<T> func() {
        return new FuncSet<>(this);
    }

    /**
     * 内部方法
     * <p>
     * 根据给定的条件进行查询
     *
     * @param sql sql语句
     * @return 实体对象List
     */
    public List<T> querySqlInternal(String sql) {
        Debugger.d("CoreDao--querySqlInternal--sql:" + sql);
        Cursor cursor = null;
        try {
            CoreDatabase cdb = cdInstance.getCoreDataBase().getReadableDatabase();
            cursor = cdb.rawQuery(sql, null);
            return bindCursor(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeCursor(cursor);
        }
        return new ArrayList<>();
    }

    /**
     * 给定sql更新或者删除
     *
     * @param sql
     * @return 是否删除成功
     */
    public boolean updateDeleteInternal(String sql) {
        try {
            Debugger.d("CoreDao--updateDeleteInternal--sql:" + sql);
            CoreDatabase cdb = cdInstance.getCoreDataBase().getWritableDatabase();
            CoreStatement cs = cdb.compileStatement(sql);
            return cs.executeUpdateDelete() > 0;
        } finally {
            sendTrigger(new QueryData(this));
        }
    }

    /**
     * 内部方法
     *
     * @param asyncCall
     * @param <T>
     * @return
     */
    public <T> AsyncFuture<T> callAsyncInternal(final AsyncCall<T> asyncCall) {
        final AsyncFuture<T> future = new AsyncFuture<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                T call = asyncCall.call();
                AsyncFuture.Callback<T> callback = future.getCallback();
                if (callback != null) {
                    callback.response(call);
                }
            }
        });
        return future;
    }

    /**
     * 内部方法
     *
     * @param sql
     * @return
     */
    public List<ContentValues> queryContentValuesInternal(String sql) {
        List<ContentValues> contentValuesList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = querySqlCursor(sql);
            while (cursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
                contentValuesList.add(contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeCursor(cursor);
        }
        return contentValuesList;
    }

    public ResultObservable<T> observable(QuerySet<T> querySet) {
        final ResultQuery<T> resultQuery = new ResultQuery<>(this, querySet);
        final Subject<QueryData> triggers = getCoreData().getTriggers();
        return triggers
                .observeOn(Schedulers.io())
                // 过滤 判断当前的 修改是否符合 当前查询
                .filter(resultQuery.getPredicateQuery())
                // 映射为ResultSet
                .map(resultQuery.getMapResult())
                // 在注册一开始就发射一组数据
                .startWith(querySet)
                // 做数据映射
                .map(resultQuery.getMapList())
                // Observable 转换
                .to(ResultObservable.<T>toFunction());
    }

    /**
     * 发送事件
     *
     * @param data 封装了当前操作集
     */
    private void sendTrigger(QueryData data) {
        getCoreData().getTriggers().onNext(data);
    }
}
