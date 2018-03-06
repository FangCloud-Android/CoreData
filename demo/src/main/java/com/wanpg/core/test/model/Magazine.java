package com.wanpg.core.test.model;

import com.coredata.annotation.Entity;
import com.coredata.annotation.PrimaryKey;

/**
 * Created by wangjinpeng on 2017/6/27.
 */
@Entity
public class Magazine extends Book {

    @PrimaryKey
    private long magazineId;


    public long getMagazineId() {
        return magazineId;
    }

    public void setMagazineId(long magazineId) {
        this.magazineId = magazineId;
    }
}
