package com.wanpg.core.test.model;

import java.io.Serializable;

/**
 * Created by wangjinpeng on 2017/6/1.
 */
public class Tag implements Serializable {

    private static final long serialVersionUID = -86325694428788590L;
    /**
     * tag id
     */
    long id;

    /**
     * tag name
     */
    String name;

    public Tag(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
