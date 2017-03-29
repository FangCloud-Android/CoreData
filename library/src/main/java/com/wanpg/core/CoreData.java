package com.wanpg.core;

import android.app.Application;

import com.wanpg.core.db.DataBaseHelper;
import com.wanpg.core.db.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangjinpeng on 2017/3/21.
 * 管理类，与用户交互，与db交互
 */
public class CoreData {

    public static class Builder {
        private ArrayList<Class<? extends CoreObject>> coreObjectTypeList;
        private String name;
        private int version;

        public Builder() {
            coreObjectTypeList = new ArrayList<>();
        }

        public Builder register(Class<? extends CoreObject>... clazz) {
            Collections.addAll(coreObjectTypeList, clazz);
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }
    }

    static List<Class<? extends CoreObject>> getCoreObjectTypeList() {
        return coreObjectTypeList;
    }

    /**
     * 保存当前环境下的数据库信息列表
     */
    private static final ArrayList<Class<? extends CoreObject>>
            coreObjectTypeList = new ArrayList<>();
    /**
     * 默认的实例
     */
    private static CoreData instance;

    /**
     * 此方法是初始化CoreData的入口
     *
     * @param builder
     */
    public synchronized static void init(Application application, Builder builder) {
        if (coreObjectTypeList.size() > 0) {
            throw new IllegalStateException("CoreData has been initialized");
        }
        instance = new CoreData(application, builder);
        coreObjectTypeList.addAll(builder.coreObjectTypeList);
    }

    /**
     * 获取默认实例
     *
     * @return
     */
    public static CoreData defaultInstance() {
        if (instance == null) {
            throw new IllegalStateException("CoreData has not been initialized, " +
                    "you must call CoreData.init(app) when Application onCreate be invoked");
        }
        return instance;
    }

    /**
     * 数据库对象实例
     */
    private DataBaseHelper dataBaseHelper;

    /**
     * 缓存的数据
     */
    private Map<Class<? extends CoreObject>, CoreCache> cachedDataMap = new ConcurrentHashMap<>();

    private HashMap<String, CoreTransaction> cacheTransactionForThread = new HashMap<>();

    public CoreTransaction beginTransaction() {
        String name = Thread.currentThread().getName();
        CoreTransaction coreTransaction = cacheTransactionForThread.get(name);
        if (coreTransaction == null) {
            coreTransaction = new CoreTransaction();
            cacheTransactionForThread.put(name, coreTransaction);
        }
        return coreTransaction;
    }

    /**
     * 注册对象
     *
     * @param datas
     */
    public void register(CoreObject... datas) {
        CoreTransaction coreTransaction = cacheTransactionForThread.get(Thread.currentThread().getName());
        if (coreTransaction == null) {
            for (CoreObject data : datas) {
                checkAndGetCoreCache(data.getClass()).registerObject(data);
            }
        } else {
            coreTransaction.registerObject(datas);
        }
    }

    /**
     * 数据刷新接口
     *
     * @param data
     */
    public <T extends CoreObject> void refresh(T data) {
        checkAndGetCoreCache(data.getClass()).refresh(data);
        // 此处插入数据库
        where(data.getClass()).update(data);
    }

    private CoreCache checkAndGetCoreCache(Class<? extends CoreObject> aClass) {
        CoreCache coreCache = cachedDataMap.get(aClass);
        if (coreCache == null) {
            coreCache = new CoreCache();
            cachedDataMap.put(aClass, coreCache);
        }
        return coreCache;
    }

    /**
     * 私有界面，创建数据库入口
     *
     * @param application
     * @param builder
     */
    private CoreData(Application application, Builder builder) {
        dataBaseHelper = new DataBaseHelper(
                application, builder.name, builder.version, getCoreObjectTypeList());
    }

    public <T extends CoreObject> QueryBuilder<T> where(Class<T> clazz) {
        return new QueryBuilder<>(clazz, dataBaseHelper.getWritableDatabase());
    }
}
