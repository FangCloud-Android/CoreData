package com.wanpg.core.test.migration;

import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.migrate.DataSet;
import com.coredata.core.db.migrate.Migration;
import com.coredata.utils.SqlUtils;

public class Migration14 implements Migration {
    @Override
    public int getStartVersion() {
        return 14;
    }

    @Override
    public void onStart(CoreDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SqlUtils.getDropTableSql("migration_model"));
    }

    @Override
    public void onEnd(CoreDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public String[] dataMigrationTables() {
        return new String[0];
    }

    @Override
    public void onDataMigrate(DataSet dataSet) {

    }
}
