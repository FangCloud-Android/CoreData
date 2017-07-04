package com.coredata.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.coredata.db.DbProperty;
import com.coredata.db.Property;
import com.coredata.utils.SqlUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Dao，实体都会拥有一个Dao实例，可进行增删改查
 *
 * @param <T> Dao对应的实体类
 */
public abstract class CoreDao<T> {

    private static final Object lock = new Object();
    private SQLiteOpenHelper dbHelper;

    /**
     * 数据库创建
     *
     * @param db {@link SQLiteOpenHelper#onCreate(SQLiteDatabase)}
     */
    void onDataBaseCreate(SQLiteDatabase db) {
        db.execSQL(getCreateTableSql());
    }

    /**
     * 数据库升级
     *
     * @param db         {@link SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)}
     * @param oldVersion 老版本
     * @param newVersion 新版本
     */
    void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        synchronized (lock) {
            List<DbProperty> dbProperties = new ArrayList<>();
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(String.format("PRAGMA TABLE_INFO(%s)", getTableName()), null);
                int nameCursorIndex = cursor.getColumnIndex("name");
                int typeCursorIndex = cursor.getColumnIndex("type");
                int nonNullCursorIndex = cursor.getColumnIndex("notnull");
                int defValCursorIndex = cursor.getColumnIndex("dflt_value");
                int primaryKeyCursorIndex = cursor.getColumnIndex("pk");
                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameCursorIndex);
                    String type = cursor.getString(typeCursorIndex);
                    boolean primaryKey = cursor.getInt(primaryKeyCursorIndex) == 1;
                    dbProperties.add(new DbProperty(name, type, primaryKey));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeCursor(cursor);
            }

            if (dbProperties.isEmpty()) {
                // 如果找不到建表语句，则删除旧表重新创建
                db.execSQL(SqlUtils.getDropTableSql(getTableName()));
                db.execSQL(getCreateTableSql());
            } else {
                // 解析原始表创建语句，分析字段对应类型及主键，索引等参数是否一致，并进行修改

                // 匹配相交的数据
                List<Property> tableProperties = getTableProperties();
                List<DbProperty> newDbProperties = new ArrayList<>();
                for (Property property : tableProperties) {
                    newDbProperties.add(
                            new DbProperty(property.name,
                                    SqlUtils.getSqlTypeByClazz(property.type),
                                    property.primaryKey));
                }

                // 以新表为基准求交集，如果新表是旧表的子集，并且size相等，说明不需要迁移表，不需要做任何处理
                // 如果求交集数据有变化，或者新旧表列数不相等，则需要迁移
                if (newDbProperties.retainAll(dbProperties)
                        || tableProperties.size() != dbProperties.size()) {
                    // 修改老表到临时表
                    String tempTableName = getTableName() + "_" + oldVersion;
                    boolean needMoveData = true;
                    try {
                        db.execSQL(String.format("ALTER TABLE %s RENAME TO %s", getTableName(), tempTableName));
                    } catch (SQLException e) {
                        // 修改表结构失败
                        db.execSQL(SqlUtils.getDropTableSql(getTableName()));
                        needMoveData = false;
                    }
                    // 创建新的表
                    db.execSQL(getCreateTableSql());
                    // 交集不为空则进行数据迁移

                    if (needMoveData && !newDbProperties.isEmpty()) {
                        try {
                            // 取出老的数据
                            boolean isJoinPrimaryKey = false;
                            for (DbProperty dbProperty : newDbProperties) {
                                if (dbProperty.primaryKey) {
                                    isJoinPrimaryKey = true;
                                    break;
                                }
                            }
                            // 如果交集中有主键，则需要数据迁移
                            // 如果主键不一致，说明数据没有迁移的必要
                            if (isJoinPrimaryKey) {
                                // 从老表中取出所有交集的数据
                                StringBuilder sqlBuilder = new StringBuilder("SELECT ");
                                boolean isFirst = true;
                                for (DbProperty dbProperty : newDbProperties) {
                                    if (!isFirst) {
                                        sqlBuilder.append(", ");
                                    }
                                    isFirst = false;
                                    sqlBuilder.append(dbProperty.name);
                                }
                                sqlBuilder.append(" FROM ").append(tempTableName);
                                List<ContentValues> contentValuesList = new ArrayList<>();
                                Cursor cursorData = null;
                                try {
                                    // 取出所有相交的数据，并转换为ContentValues
                                    cursorData = db.rawQuery(sqlBuilder.toString(), null);
                                    while (cursorData.moveToNext()) {
                                        ContentValues contentValues = new ContentValues();
                                        DatabaseUtils.cursorRowToContentValues(cursorData, contentValues);
                                        contentValuesList.add(contentValues);
                                    }
                                } finally {
                                    closeCursor(cursorData);
                                }
                                // 插入新表
                                if (!contentValuesList.isEmpty()) {
                                    db.beginTransaction();
                                    for (ContentValues cv : contentValuesList) {
                                        db.replace(getTableName(), null, cv);
                                    }
                                    db.setTransactionSuccessful();
                                    db.endTransaction();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // 删除旧表
                    db.execSQL(SqlUtils.getDropTableSql(tempTableName));
                }
            }
        }
    }

    /**
     * 数据库降级
     *
     * @param db         {@link SQLiteOpenHelper#onDowngrade(SQLiteDatabase, int, int)}
     * @param oldVersion 老版本
     * @param newVersion 新版本
     */
    void onDataBaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SqlUtils.getDropTableSql(getTableName()));
        onDataBaseCreate(db);
    }

    /**
     * 数据库创建
     *
     * @param dbHelper 数据库管理类
     */
    protected void onCreate(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    protected abstract String getTableName();

    protected abstract String getPrimaryKeyName();

    protected abstract List<Property> getTableProperties();

    protected abstract String getCreateTableSql();

    /**
     * 获取插入语句
     *
     * @return 返回插入数据库语句，例如"INSERT OR REPLACE INTO `Book`(`id`,`name`,`tags`,`author_id`,`desc_content`,`desc_email`) VALUES (?,?,?,?,?,?)"
     */
    protected abstract String getInsertSql();

    protected abstract void bindStatement(SQLiteStatement statement, T t);

    protected abstract boolean replaceInternal(Collection<T> tCollection, SQLiteDatabase db);

    protected abstract List<T> bindCursor(Cursor cursor);

    /**
     * 单条数据插入 内部使用
     *
     * @param t  实体对象
     * @param db SQLiteDatabase对象
     * @return 是否插入成功
     */
    protected boolean replace(T t, SQLiteDatabase db) {
        // 查找出 所有关联的对象对应的dao，以及所对应的dao
        ArrayList<T> tList = new ArrayList<>();
        tList.add(t);
        return replace(tList, db);
    }

    /**
     * 插入集合数据, 内部使用
     *
     * @param tCollection 实体集合
     * @param db          SQLiteDatabase对象
     * @return 是否插入成功
     */
    public boolean replace(Collection<T> tCollection, SQLiteDatabase db) {
        return replaceInternal(tCollection, db);
    }

    /**
     * 绑定数据并提交插入, 内部使用
     *
     * @param tList 实例List
     * @param db    SQLiteDatabase对象
     * @return 是否插入成功
     */
    protected boolean executeInsert(List<T> tList, SQLiteDatabase db) {
        SQLiteStatement statement = db.compileStatement(getInsertSql());
        for (T t : tList) {
            bindStatement(statement, t);
            statement.executeInsert();
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
        synchronized (lock) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            replace(t, db);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return true;
    }

    /**
     * 插入集合数据
     *
     * @param tCollection 实体集合
     * @return 是否插入成功
     */
    public boolean replace(Collection<T> tCollection) {
        synchronized (lock) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            replace(tCollection, db);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return true;
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
        List<T> tList = queryByKeys(key);
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
    public List<T> queryByKeys(Object... keys) {
        if (keys == null || keys.length <= 0) {
            return new ArrayList<>();
        }
        return query()
                .where(getPrimaryKeyName()).in(keys)
                .result();
    }

    /**
     * 根据给定的条件进行查询
     *
     * @param sql sql语句
     * @return 实体对象List
     */
    List<T> querySqlInternal(String sql) {
        synchronized (lock) {
            Log.d("CoreData", "CoreDao--querySqlInternal--sql:" + sql);
            SQLiteDatabase db;
            Cursor cursor = null;
            try {
                db = dbHelper.getReadableDatabase();
                cursor = db.rawQuery(sql, null);
                return bindCursor(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeCursor(cursor);
            }
            return new ArrayList<>();
        }
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
    public boolean deleteByKeys(Object... keys) {
        if (keys == null || keys.length <= 0) {
            return false;
        }
        return delete()
                .where(getPrimaryKeyName()).in(keys)
                .execute();
    }

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
     * 给定sql更新或者删除
     *
     * @param sql
     * @return 是否删除成功
     */
    boolean updateDeleteInternal(String sql) {
        synchronized (lock) {
            Log.d("CoreData", "CoreDao--updateDeleteInternal--sql:" + sql);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            SQLiteStatement s = db.compileStatement(sql);
            return s.executeUpdateDelete() > 0;
        }
    }

    /**
     * 关闭游标
     *
     * @param cursor 游标
     */
    private void closeCursor(Cursor cursor) {
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
