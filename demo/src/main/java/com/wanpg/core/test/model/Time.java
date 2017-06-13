package com.wanpg.core.test.model;

import com.coredata.annotation.Entity;
import com.coredata.annotation.PrimaryKey;

/**
 * Created by wangjinpeng on 2017/6/8.
 */
@Entity
public class Time {

    @PrimaryKey
    private long timemills;

    public long getTimemills() {
        return timemills;
    }

    public void setTimemills(long timemills) {
        this.timemills = timemills;
    }
}
