package com.wanpg.core.test.model;

import com.coredata.annotation.Entity;
import com.coredata.annotation.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangjinpeng on 2017/6/27.
 */
@Entity
@Setter
@Getter
public class Magazine extends Book {

    @PrimaryKey
    private long magazineId;

}
