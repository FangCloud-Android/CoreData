package com.coredata.core;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Dao，实体都会拥有一个Dao实例，可进行增删改查
 *
 * @param <T>
 */
public abstract class CoreDao<T> {

    private SQLiteOpenHelper dbHelper;

    /**
     * 数据库创建
     *
     * @param db
     */
    void onDataBaseCreate(SQLiteDatabase db) {
        db.execSQL(getCreateTableSql());
    }

    /**
     * 数据库升级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String originTableCreateSql = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format("SELECT sql FROM sqlite_master WHERE type='table' AND name='%s'", getTableName()), null);
            if (cursor.moveToNext()) {
                originTableCreateSql = cursor.getString(cursor.getColumnIndex("sql"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (TextUtils.isEmpty(originTableCreateSql)) {
            db.execSQL("DROP TABLE " + getTableName());
            db.execSQL(getCreateTableSql());
        } else {
            // 解析原始表创建语句，分析字段对应类型及主键，索引等参数是否一致，并进行修改
        }
    }

    /**
     * 数据库降级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    void onDataBaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + getTableName());
        onDataBaseCreate(db);
    }

    /**
     * 数据库创建
     *
     * @param dbHelper
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
     * "INSERT OR REPLACE INTO `Book`(`id`,`name`,`tags`,`author_id`,`desc_content`,`desc_email`) VALUES (?,?,?,?,?,?)"
     *
     * @return
     */
    protected abstract String getInsertSql();

    protected abstract void bindStatement(SQLiteStatement statement, T t);

    protected abstract boolean replaceInternal(Collection<T> tCollection, SQLiteDatabase db);

    protected abstract List<T> bindCursor(Cursor cursor);

    /**
     * 单条数据插入 内部使用
     *
     * @param t
     * @param db
     * @return
     */
    protected boolean replace(T t, SQLiteDatabase db) {
        // 查找出 所有关联的对象对应的dao，以及所对应的dao
        List<T> tList = new ArrayList<>();
        tList.add(t);
        return replace(tList, db);
    }

    /**
     * 插入集合数据, 内部使用
     *
     * @param tCollection
     * @param db
     * @return
     */
    public boolean replace(Collection<T> tCollection, SQLiteDatabase db) {
        return replaceInternal(tCollection, db);
    }

    /**
     * 绑定数据并提交插入
     *
     * @param tList
     * @param db
     * @return
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
     * @param t
     * @return
     */
    public boolean replace(T t) {
        if (t == null) {
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        replace(t, db);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }

    /**
     * 插入集合数据
     *
     * @param tCollection
     * @return
     */
    public boolean replace(Collection<T> tCollection) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        replace(tCollection, db);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public List<T> queryAll() {
        return queryWhere(null);
    }

    /**
     * 根据主键查询数据
     *
     * @param key
     * @return
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
     * @param keys
     * @return
     */
    public List<T> queryByKeys(Object... keys) {
        StringBuilder append = new StringBuilder();
        boolean isFirst = true;
        for (Object key : keys) {
            if (isFirst) {
                isFirst = false;
            } else {
                append.append(",");
            }
            append.append(key);
        }
        return queryWhere(String.format("%s.%s in (%s)",
                getTableName(),
                getPrimaryKeyName(),
                append));
    }

    /**
     * 根据给定的条件进行查询
     *
     * @param whereClause
     * @return
     */
    public List<T> queryWhere(String whereClause) {
        String sql = "SELECT * FROM " + getTableName() + (TextUtils.isEmpty(whereClause) ? "" : " WHERE " + whereClause);
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
        return null;
    }

    /**
     * 删除全部
     *
     * @return
     */
    public boolean deleteAll() {
        return deleteWhere(null);
    }

    /**
     * 根据给定主键删除
     *
     * @param key
     */
    public boolean deleteByKey(Object key) {
        return deleteWhere(
                String.format(
                        "%s.%s = %s",
                        getTableName(),
                        getPrimaryKeyName(),
                        String.valueOf(key)));
    }

    /**
     * 根据给定主键列表删除
     *
     * @param keys
     */
    public boolean deleteByKeys(Object... keys) {
        StringBuilder append = new StringBuilder();
        boolean isFirst = true;
        for (Object key : keys) {
            if (isFirst) {
                isFirst = false;
            } else {
                append.append(",");
            }
            append.append(key);
        }
        return deleteWhere(String.format("%s.%s in (%s)",
                getTableName(),
                getPrimaryKeyName(),
                append));
    }

    /**
     * 根据给定条件进行删除
     *
     * @param whereClause
     * @return
     */
    public boolean deleteWhere(String whereClause) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(getTableName(), whereClause, null) > 0;
    }

    /**
     * 关闭游标
     *
     * @param cursor
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
