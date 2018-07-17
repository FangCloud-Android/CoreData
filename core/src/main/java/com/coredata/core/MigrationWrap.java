package com.coredata.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.migrate.DataSet;
import com.coredata.core.db.migrate.Migration;
import com.coredata.core.utils.DBUtils;
import com.coredata.db.DbProperty;
import com.coredata.db.Property;
import com.coredata.utils.SqlUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 此处做数据库的升级和降级处理
 */
public final class MigrationWrap {

    private List<Migration> migrations;
    // 需要数据迁移的数据
    private Map<String, DataSet> dataSetMap = new HashMap<>();
    private final Map<Class, CoreDao> coreDaoHashMap;

    MigrationWrap(List<Migration> migrations, Map<Class, CoreDao> coreDaoHashMap) {
        this.migrations = migrations;
        this.coreDaoHashMap = coreDaoHashMap;
        if (this.migrations != null) {
            Collections.sort(this.migrations, new Comparator<Migration>() {
                @Override
                public int compare(Migration o1, Migration o2) {
                    return o1.getStartVersion() - o2.getStartVersion();
                }
            });
        }
    }

    /**
     * 数据库升级
     *
     * @param cdb        {@link CoreDatabaseManager#onUpgrade(CoreDatabase, int, int)}
     * @param oldVersion 老版本
     * @param newVersion 新版本
     */
    void onUpgrade(CoreDatabase cdb, int oldVersion, int newVersion) {
        // 此处做自定义的数据迁移
        onDataMigrateStart(cdb, oldVersion, newVersion);
        // 先取出老的表结构，要在onDataMigrateStart后执行，因为自定义migration可能会删除部分表
        List<String> originTableList = DBUtils.tableList(cdb);
        // 如果存在执行升级，不存在执行创建
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            CoreDao value = entry.getValue();
            boolean remove = originTableList.remove(value.getTableName());
            if (remove) {
                onDataBaseUpgradeAuto(cdb, value, oldVersion, newVersion, dataSetMap.get(value.getTableName()));
            } else {
                onDataBaseCreate(cdb, value);
            }
        }
        // 剩下的删除表
        for (String leftTableName : originTableList) {
            DBUtils.dropTable(cdb, leftTableName);
        }
        onDataMigrateEnd(cdb, oldVersion, newVersion);
    }

    /**
     * 数据库降级
     *
     * @param cdb        {@link CoreDatabaseManager#onDowngrade(CoreDatabase, int, int)}
     * @param oldVersion 老版本
     * @param newVersion 新版本
     */
    void onDowngrade(CoreDatabase cdb, int oldVersion, int newVersion) {
        // 目前降级操作时删除所有表，并重新创建
        // 先取出老的表结构
        List<String> originTableList = DBUtils.tableList(cdb);
        // 如果存在执行降级，不存在执行创建
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            CoreDao value = entry.getValue();
            boolean remove = originTableList.remove(value.getTableName());
            if (remove) {
                onDataBaseDowngradeAuto(cdb, value, oldVersion, newVersion);
            } else {
                onDataBaseCreate(cdb, value);
            }
        }
        // 剩下的删除表
        for (String leftTableName : originTableList) {
            DBUtils.dropTable(cdb, leftTableName);
        }
    }

    /**
     * 数据迁移开始
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    private void onDataMigrateStart(CoreDatabase db, int oldVersion, int newVersion) {
        // 执行数据数据迁移
        if (migrations == null) {
            return;
        }
        List<String> originTableList = DBUtils.tableList(db);
        // 保留需要迁移的migration，过滤掉版本过低的迁移
        Iterator<Migration> iterator = migrations.iterator();
        while (iterator.hasNext()) {
            Migration next = iterator.next();
            int startVersion = next.getStartVersion();
            if (startVersion <= oldVersion || startVersion > newVersion) {
                iterator.remove();
            }
        }

        // 如果migrations为空，则不需要做自定义迁移
        if (migrations.isEmpty()) {
            return;
        }

        for (Migration migration : migrations) {
            // migration 开始
            migration.onStart(db, oldVersion, newVersion);
            // 获取当前migration会变更的表
            String[] migrationTables = migration.dataMigrationTables();
            if (migrationTables != null && migrationTables.length > 0) {
                for (String table : migrationTables) {
                    // 取出变更表的数据
                    DataSet dataSet = dataSetMap.get(table);
                    if (dataSet == null) {
                        List<ContentValues> contentValuesList = null;
                        if (originTableList.contains(table)) {
                            contentValuesList = DBUtils.queryAll(db, table);
                        }
                        dataSet = new DataSet(table, contentValuesList);
                        dataSetMap.put(table, dataSet);
                    }
                    // 调用migration
                    migration.onDataMigrate(dataSet);
                    // 表名和数据都会有可能进行变更
                    // 数据不用管，当表名变更时map中的key也要做变更
                    if (!dataSet.getTableName().equals(table)) {
                        dataSetMap.remove(table);
                        dataSetMap.put(dataSet.getTableName(), dataSet);
                    }
                }
            }
        }
    }

    /**
     * 数据迁移结束
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    private void onDataMigrateEnd(CoreDatabase db, int oldVersion, int newVersion) {
        if (migrations == null || migrations.isEmpty()) {
            return;
        }
        // 插入数据
        if (!dataSetMap.isEmpty()) {
            // 获取到当前所有的table表名
            List<String> tableList = DBUtils.tableList(db);
            Set<Map.Entry<String, DataSet>> entries = dataSetMap.entrySet();
            for (Map.Entry<String, DataSet> entry : entries) {
                // 此处要判断是否是所有的字段都需要插入
                DataSet value = entry.getValue();
                if (value != null && !value.isEmpty()) {
                    String tableName = value.getTableName();
                    if (tableList.contains(tableName)) {
                        Set<String> columnNames = getColumnNames(db, tableName);
                        db.beginTransaction();
                        for (ContentValues contentValues : value) {
                            // 过滤一次ContentValues
                            db.replace(tableName, null, filterContentValues(contentValues, columnNames));
                        }
                        db.setTransactionSuccessful();
                        db.endTransaction();
                    }
                }
            }
        }
        // 执行onEnd生命周期
        for (Migration migration : migrations) {
            int startVersion = migration.getStartVersion();
            if (startVersion > oldVersion && startVersion <= newVersion) {
                migration.onEnd(db, oldVersion, newVersion);
            }
        }
        migrations.clear();
    }


    /**
     * 数据库创建
     *
     * @param db {@link CoreDatabaseManager#onCreate(CoreDatabase)}
     */
    void onDataBaseCreate(CoreDatabase db, CoreDao coreDao) {
        db.execSQL(coreDao.getCreateTableSql());
    }

    /**
     * 数据库自动升级
     * <p>
     * 步骤：<br>
     * 1、查出现有表结构
     * 2、如果不存在表结构，则直接创建表
     * 3、如果存在则判断old表结构与new表结构是否一致
     * 4、
     *
     * @param db
     * @param coreDao
     * @param oldVersion
     * @param newVersion
     */
    private void onDataBaseUpgradeAuto(CoreDatabase db, CoreDao coreDao, int oldVersion, int newVersion, DataSet dataSet) {
        synchronized (CoreDao.lock) {
            // 查出old表结构
            List<DbProperty> oldDbProperties = null;
            // 如果dataSet存在，则说明开启了手动变更数据，不再执行自动升级，直接清空数据，并创建新表
            if (dataSet == null) {
                oldDbProperties = pragmaTableInfo(db, coreDao.getTableName());
            }
            if (oldDbProperties == null || oldDbProperties.isEmpty()) {
                // 如果不存在表结构，则尝试删除旧表并重新创建
                db.execSQL(SqlUtils.getDropTableSql(coreDao.getTableName()));
                db.execSQL(coreDao.getCreateTableSql());
            } else {
                // 如果存在则判断old表结构与new表结构是否一致
                // 解析原始表创建语句，分析字段对应类型及主键，索引等参数是否一致，并进行修改

                // 匹配相交的数据
                @SuppressWarnings("unchecked")
                List<Property> newTableProperties = coreDao.getTableProperties();
                // 转换Property为DbProperty
                List<DbProperty> newDbProperties = new ArrayList<>();
                for (Property property : newTableProperties) {
                    newDbProperties.add(
                            new DbProperty(property.name,
                                    SqlUtils.getSqlTypeByClazz(property.type),
                                    property.primaryKey));
                }

                // 以新表为基准求交集，如果新表是旧表的子集，并且size相等，说明不需要迁移表，不需要做任何处理
                // 如果求交集数据有变化，或者新旧表列数不相等，则需要迁移
                // newDbProperties.retainAll(oldDbProperties)
                // -- true -- 说明新增了字段，new表中某个字段old表中没有
                // -- false -- 说明字段未变更 或者 删除了字段，新表所有字段都存在与老表
                // false时，再判断一次new表size 是否与old表size相同，相同则说明数据不变更，不相同则说明是删除了字段
                if (newDbProperties.retainAll(oldDbProperties)
                        || newTableProperties.size() != oldDbProperties.size()) {
                    // old表改名为temp表
                    String tempTableName = coreDao.getTableName() + "_" + oldVersion;
                    boolean needMoveData = true;
                    try {
                        db.execSQL(String.format("ALTER TABLE %s RENAME TO %s", coreDao.getTableName(), tempTableName));
                    } catch (SQLException e) {
                        // 修改表结构失败
                        db.execSQL(SqlUtils.getDropTableSql(coreDao.getTableName()));
                        needMoveData = false;
                    }

                    // 创建新的表
                    db.execSQL(coreDao.getCreateTableSql());

                    // 交集不为空则进行数据迁移
                    if (needMoveData && !newDbProperties.isEmpty()) {
                        try {
                            // 判断交集中是否有主键
                            boolean isJoinPrimaryKey = false;
                            for (DbProperty dbProperty : newDbProperties) {
                                if (dbProperty.primaryKey) {
                                    isJoinPrimaryKey = true;
                                    break;
                                }
                            }

                            // 如果交集中有主键，则需要数据迁移
                            // 如果主键不一致，说明主键变更，数据没有迁移的必要
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
                                List<ContentValues> contentValuesList = DBUtils.queryBySql(db, sqlBuilder.toString());
                                // 插入新表
                                if (!contentValuesList.isEmpty()) {
                                    db.beginTransaction();
                                    for (ContentValues cv : contentValuesList) {
                                        db.replace(coreDao.getTableName(), null, cv);
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
     * 数据库自动降级
     *
     * @param db
     * @param coreDao
     * @param oldVersion
     * @param newVersion
     */
    private void onDataBaseDowngradeAuto(CoreDatabase db, CoreDao coreDao, int oldVersion, int newVersion) {
        db.execSQL(SqlUtils.getDropTableSql(coreDao.getTableName()));
        onDataBaseCreate(db, coreDao);
    }

    private ContentValues filterContentValues(ContentValues cv, Set<String> columnNames) {
        // 保留相交的数据
        // keySet与Collection的迭代器一样，可以直接修改map中的源数据
        cv.keySet().retainAll(columnNames);
        return cv;
    }

    private List<DbProperty> pragmaTableInfo(CoreDatabase db, String tableName) {
        List<DbProperty> dbProperties = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format("PRAGMA TABLE_INFO(%s)", tableName), null);
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
            DBUtils.closeCursor(cursor);
        }
        return dbProperties;
    }

    private Set<String> getColumnNames(CoreDatabase db, String tableName) {
        Set<String> nameSet = new HashSet<>();
        List<DbProperty> dbProperties = pragmaTableInfo(db, tableName);
        for (DbProperty dbProperty : dbProperties) {
            nameSet.add(dbProperty.name);
        }
        return nameSet;
    }
}
