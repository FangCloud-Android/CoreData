package com.coredata.core.db;

/**
 * 数据库迁移接口，实现此接口来实现个数据库版本的数据库迁移、升级、修订
 * <p>
 * package 变更 请使用 {@link com.coredata.core.db.migrate.Migration}
 */
@Deprecated
public interface Migration extends com.coredata.core.db.migrate.Migration {

}
