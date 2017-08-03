package com.coredata.core;

import android.app.Application;

import com.coredata.core.utils.ReflectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangjinpeng on 2017/6/3.
 */

public final class CoreData {

    public static class Builder {
        private ArrayList<Class<?>> coreObjectTypeList;
        private String name;
        private int version;
        private String password;

        public static Builder builder() {
            return new Builder();
        }

        private Builder() {
            coreObjectTypeList = new ArrayList<>();
        }

        public Builder register(Class<?>... clazz) {
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

        public Builder password(String password) {
            this.password = password;
            return this;
        }
    }

    /**
     * 默认的实例
     */
    private static CoreData instance;

    /**
     * 此方法是初始化CoreData的入口
     *
     * @param application 给定Application上下文
     * @param builder     {@link Builder}构建工具
     */
    public synchronized static void init(Application application, Builder builder) {
        if (instance != null) {
            throw new IllegalStateException("CoreData has been initialized");
        }
        instance = new CoreData(builder);
        instance.onCreate(application);
    }

    /**
     * 获取默认实例
     *
     * @return 返回一个默认且唯一的实例
     */
    public static CoreData defaultInstance() {
        if (instance == null) {
            throw new IllegalStateException("CoreData has not been initialized, " +
                    "you must call CoreData.onCreate(app) when Application onCreate be invoked");
        }
        return instance;
    }

    private CoreDatabaseManager coreDataBaseManager;

    private HashMap<Class, CoreDao> coreDaoHashMap = new HashMap<>();

    private Builder builder;

    /**
     * 私有界面，创建数据库入口
     *
     * @param builder
     */
    private CoreData(Builder builder) {
        this.builder = builder;
    }

    private <T> CoreDao<T> initializeDao(Class<T> coreDaoClass) {
        return ReflectUtils.getGeneratedEntityDaoImpl(coreDaoClass);
    }

    private void onCreate(Application application) {
        // 此处找到相应的dao
        for (Class<?> entityClass : builder.coreObjectTypeList) {
            coreDaoHashMap.put(entityClass, initializeDao(entityClass));
        }
        coreDataBaseManager = new CoreDatabaseManager(
                application,
                builder.name,
                builder.version,
                coreDaoHashMap,
                builder.password);
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            entry.getValue().onCreate(coreDataBaseManager);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> CoreDao<T> dao(Class<T> clazz) {
        return coreDaoHashMap.get(clazz);
    }

    public CoreDatabaseManager getCoreDataBase() {
        return coreDataBaseManager;
    }
}
