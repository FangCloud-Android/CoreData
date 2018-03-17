package com.wanpg.core.test.model;

import com.coredata.annotation.ColumnInfo;
import com.coredata.annotation.Entity;
import com.coredata.annotation.PrimaryKey;

/**
 * Created by wangjinpeng on 2017/6/27.
 */
@Entity(tableName = "maga", primaryKey = "magazine_id")
public class Magazine extends Book {

    @ColumnInfo(name = "magazine_id")
    private long magazineId;


    public long getMagazineId() {
        return magazineId;
    }

    public void setMagazineId(long magazineId) {
        this.magazineId = magazineId;
    }
}
