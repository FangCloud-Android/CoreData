package com.wanpg.core.test.migration;

import android.content.ContentValues;

import com.coredata.core.db.CoreDatabase;
import com.coredata.core.db.migrate.DataSet;
import com.coredata.core.db.migrate.Migration;

public class Migration16 implements Migration {
    @Override
    public int getStartVersion() {
        return 16;
    }

    @Override
    public void onStart(CoreDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onEnd(CoreDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public String[] dataMigrationTables() {
        return new String[]{"MigrationModel"};
    }

    @Override
    public void onDataMigrate(DataSet dataSet) {
        for (ContentValues cv : dataSet) {
            int id = cv.getAsInteger("id");
            cv.put("id", String.valueOf(id) + "_migration");
            String name = cv.getAsString("name");
            cv.remove("name");
            cv.put("new_name", name);
        }
    }
}
