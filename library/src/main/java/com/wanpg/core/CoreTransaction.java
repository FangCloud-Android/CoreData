package com.wanpg.core;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wangjinpeng on 2017/3/21.
 */

public class CoreTransaction {

    /**
     * 缓存的数据
     */
    private final ArrayList<CoreObject> cachedData;

    public CoreTransaction() {
        cachedData = new ArrayList<>();
    }

    public void registerObject(CoreObject... t) {
        Collections.addAll(cachedData, t);
    }

    public void commit() {
        CoreObject[] objects = new CoreObject[cachedData.size()];
        cachedData.toArray(objects);
        CoreData.defaultInstance().register(objects);
    }
}
