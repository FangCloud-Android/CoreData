package com.coredata.core.db;

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
}
