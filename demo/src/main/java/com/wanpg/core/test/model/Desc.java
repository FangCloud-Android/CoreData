package com.wanpg.core.test.model;

import com.coredata.annotation.ColumnInfo;

/**
 * Created by wangjinpeng on 2017/6/1.
 */

public class Desc {

    public Desc() {
    }

    public Desc(String content, String email) {
        this.content = content;
        this.email = email;
    }

    @ColumnInfo(name = "desc_content")
    public String content;

    @ColumnInfo(name = "desc_email")
    public String email;

}
