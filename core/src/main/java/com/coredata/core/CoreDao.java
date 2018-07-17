package com.coredata.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.CoreStatement;
import com.coredata.core.rx.QueryData;
import com.coredata.core.rx.ResultObservable;
import com.coredata.core.rx.ResultQuery;
import com.coredata.core.utils.DBUtils;
import com.coredata.core.utils.Debugger;
import com.coredata.db.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static final Object lock = new Object();
    private CoreData cdInstance;

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

    protected abstract String getCreateTableSql();

    /**
     * 获取插入语句
     *
     * @return 返回插入数据库语句，例如"INSERT OR REPLACE INTO `Book`(`id`,`name`,`tags`,`author_id`,`desc_content`,`desc_email`) VALUES (?,?,?,?,?,?)"
     */
    protected abstract String getInsertSql();

    protected abstract void bindStatement(CoreStatement statement, T t);

    protected abstract boolean replaceInternal(Collection<T> tCollection, CoreDatabase db);

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
     * 插入集合数据, 内部使用
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
            synchronized (lock) {
                CoreDatabase cdb = cdInstance.getCoreDataBase().getWritableDatabase();
                cdb.beginTransaction();
                replace(t, cdb);
                cdb.setTransactionSuccessful();
                cdb.endTransaction();
            }
            return true;
        } finally {
            sendTrigger(new QueryData(this));
        }
    }

    /**
     * 插入集合数据
     *
     * @param tCollection 实体集合
     * @return 是否插入成功
     */
    public boolean replace(Collection<T> tCollection) {
        try {
            synchronized (lock) {
                CoreDatabase cdb = cdInstance.getCoreDataBase().getWritableDatabase();
                cdb.beginTransaction();
                replace(tCollection, cdb);
                cdb.setTransactionSuccessful();
                cdb.endTransaction();
            }
            return true;
        } finally {
            sendTrigger(new QueryData(this));
        }
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
     * 查询，生成一个查询用的结构处理集
     *
     * @return 创建一个结果处理集合
     */
    public ResultSet<T> query() {
        return new ResultSet<>(this);
    }

    /**
     * 根据给定的条件进行查询
     *
     * @param sql sql语句
     * @return 实体对象List
     */
    public Cursor querySqlCursor(String sql) {
        synchronized (lock) {
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
     * 根据给定的条件进行查询
     *
     * @param sql sql语句
     * @return 实体对象List
     */
    List<T> querySqlInternal(String sql) {
        synchronized (lock) {
            Debugger.d("CoreDao--querySqlInternal--sql:" + sql);
            CoreDatabase cdb;
            Cursor cursor = null;
            try {
                cdb = cdInstance.getCoreDataBase().getReadableDatabase();
                cursor = cdb.rawQuery(sql, null);
                return bindCursor(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DBUtils.closeCursor(cursor);
            }
            return new ArrayList<>();
        }
    }

    /**
     * 给定sql更新或者删除
     *
     * @param sql
     * @return 是否删除成功
     */
    boolean updateDeleteInternal(String sql) {
        try {
            synchronized (lock) {
                Debugger.d("CoreDao--updateDeleteInternal--sql:" + sql);
                CoreDatabase cdb = cdInstance.getCoreDataBase().getWritableDatabase();
                CoreStatement cs = cdb.compileStatement(sql);
                return cs.executeUpdateDelete() > 0;
            }
        } finally {
            sendTrigger(new QueryData(this));
        }
    }

    /**
     * 给定sql查询结果并转换为ContentValues
     *
     * @param sql
     * @return
     */
    List<ContentValues> queryContentValuesInternal(String sql) {
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

    ResultObservable<T> observable(ResultSet<T> resultSet) {
        final ResultQuery<T> resultQuery = new ResultQuery<>(this, resultSet);
        final Subject<QueryData> triggers = getCoreData().getTriggers();
        return triggers
                .observeOn(Schedulers.io())
                // 过滤 判断当前的 修改是否符合 当前查询
                .filter(resultQuery.getPredicateQuery())
                // 映射为ResultSet
                .map(resultQuery.getMapResult())
                // 在注册一开始就发射一组数据
                .startWith(resultSet)
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
