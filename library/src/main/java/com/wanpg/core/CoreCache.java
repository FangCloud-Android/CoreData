package com.wanpg.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 核心缓存模块
 */

public class CoreCache {

    /**
     * 缓存的数据
     */
    private Map<Object, List<WeakReference<CoreObject>>> cachedData = new ConcurrentHashMap<>();

    public synchronized void registerObject(CoreObject t) {
        Object primaryKey = t.getPrimaryKey();
        checkAndGetList(primaryKey).add(new WeakReference<>(t));
    }

    public void refresh(CoreObject t) {
        Object primaryKey = t.getPrimaryKey();
        List<WeakReference<CoreObject>> list = checkAndGetList(primaryKey);
        boolean needAdd = true;
        if (list.isEmpty()) {
            needAdd = true;
        } else {
            ListIterator<WeakReference<CoreObject>> listIterator = list.listIterator();
            while (listIterator.hasNext()) {
                WeakReference<? super CoreObject> next = listIterator.next();
                CoreObject object = (CoreObject) next.get();
                if (object == null) {
                    listIterator.remove();
                } else {
                    if (t == object) {
                        needAdd = false;
                    } else {
                        object._update(t);
                    }
                }
            }
        }
        if (needAdd) {
            list.add(new WeakReference<>(t));
        }
    }

    private List<WeakReference<CoreObject>> checkAndGetList(Object primaryKey) {
        List<WeakReference<CoreObject>> list = cachedData.get(primaryKey);
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<WeakReference<CoreObject>>());
            cachedData.put(primaryKey, list);
        }
        return list;
    }
}
