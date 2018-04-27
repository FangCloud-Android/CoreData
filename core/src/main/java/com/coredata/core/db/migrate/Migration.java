package com.coredata.core.db.migrate;

import com.coredata.core.db.CoreDatabase;

import io.reactivex.annotations.NonNull;

/**
 * 数据库迁移接口，实现此接口来实现个数据库版本的数据库迁移、升级、修订
 */
public interface Migration {

    /**
     * 当前migration需要执行的起始版本
     */
    int getStartVersion();

    /**
     * migration开始，此方法在各个CoreDao的自动migration之前执行
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    void onStart(CoreDatabase db, int oldVersion, int newVersion);

    /**
     * migration结束，此方法在各个CoreDao的自动migration之后执行
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    void onEnd(CoreDatabase db, int oldVersion, int newVersion);

    /**
     * 返回需要做数据迁移的表名
     * <p>
     * 相应的表的数据会以{@link DataSet}形式传入{@link #onDataMigrate(DataSet)}
     *
     * @return 需要当前Migration处理的表
     */
    String[] dataMigrationTables();

    /**
     * 执行数据迁移，传入数据集合，对数据集合及其关联的表名进行变更
     *
     * @param dataSet 需要处理的表的数据集合
     */
    void onDataMigrate(@NonNull DataSet dataSet);
}
