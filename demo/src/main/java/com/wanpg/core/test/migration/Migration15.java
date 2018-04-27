package com.wanpg.core.test.migration;

import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.migrate.DataSet;
import com.coredata.core.db.migrate.Migration;

public class Migration15 implements Migration {
    @Override
    public int getStartVersion() {
        return 15;
    }

    @Override
    public void onStart(CoreDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onEnd(CoreDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public String[] dataMigrationTables() {
        return new String[]{"migration_model"};
    }

    @Override
    public void onDataMigrate(DataSet dataSet) {
        // 变更表名
        dataSet.setTableName("MigrationModel");
    }
}
