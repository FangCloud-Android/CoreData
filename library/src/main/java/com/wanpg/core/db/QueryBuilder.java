package com.wanpg.core.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wanpg.core.CoreObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjinpeng on 2017/3/21.
 */

public class QueryBuilder<T extends CoreObject> {

    private Class<T> tClass;
    private SQLiteDatabase sqLiteDatabase;

    public QueryBuilder(Class<T> tClass, SQLiteDatabase sqLiteDatabase) {
        this.tClass = tClass;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public QueryBuilder<T> equals(String columnName, Object value) {
        return this;
    }


    /**
     * 根据条件找到相应的结果
     *
     * @return
     */
    public List<T> find() {
//        Cursor cursor = sqLiteDatabase.rawQuery("", new String[]{});
//        if(cursor.moveToNext()){
//            cursor.getColumnIndex()
//        }
//        cursor.close();
        return new ArrayList<>();
    }

    /**
     * 更新一条数据
     *
     * @param data
     * @return
     */
    public boolean update(CoreObject data) {
//        sqLiteDatabase.update();
        return true;
    }
}
