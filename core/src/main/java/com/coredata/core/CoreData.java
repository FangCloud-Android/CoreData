package com.coredata.core;

import android.app.Application;

import com.coredata.core.db.Migration;
import com.coredata.core.io.ObjectInputStreamWrap;
import com.coredata.core.utils.ReflectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CoreData核心类，用于构建实例，初始化配置等等
 */
public final class CoreData {

    public static class Builder {
        private String tag = TAG_DEFAULT_INSTANCE;
        private ArrayList<Class<?>> coreObjectTypeList;
        private String name;
        private int version;
        private String password;
        @Deprecated
        private Migration migration;
        private List<Migration> migrations = new ArrayList<>();

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

        /**
         * 数据库名称，可以是一个路径
         *
         * @param name
         * @return
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 数据库版本
         *
         * @param version
         * @return
         */
        public Builder version(int version) {
            this.version = version;
            return this;
        }

        /**
         * 数据库密码，当设置此密码，默认开启数据库加密，要引用相应的库
         *
         * @param password
         * @return
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * 添加migration，已失效，请使用{@link #addMigration(Migration)}代替
         *
         * @param migration
         * @return
         */
        @Deprecated
        public Builder migration(Migration migration) {
            this.migration = migration;
            return this;
        }

        /**
         * 添加migration, 配置migration及其起始版本
         *
         * @param migration migration脚本
         * @return
         */
        public Builder addMigration(Migration migration) {
            migrations.add(migration);
            return this;
        }

        /**
         * 添加tag，用于区分CoreData实例，用于动态创建CoreData实例
         *
         * @param tag
         * @return
         */
        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }
    }

    /**
     * 默认实例的tag
     */
    private static final String TAG_DEFAULT_INSTANCE = "default";

    /**
     * 实例集合
     */
    private static Map<String, CoreData> instanceMap = new ConcurrentHashMap<>();

    /**
     * 此方法是初始化CoreData的入口
     *
     * @param application 给定Application上下文
     * @param builder     {@link Builder}构建工具
     */
    public synchronized static void init(Application application, Builder builder) {
        CoreData instance = instanceMap.get(builder.tag);
        if (instance != null) {
            throw new IllegalStateException("CoreData(tag = " + builder.tag + ") has been initialized");
        }
        instance = new CoreData(builder);
        instanceMap.put(builder.tag, instance);
        instance.onCreate(application);
    }

    /**
     * 获取默认实例
     *
     * @return 返回一个默认且唯一的实例
     */
    public synchronized static CoreData defaultInstance() {
        return instance(TAG_DEFAULT_INSTANCE);
    }

    /**
     * 根据指定的key获取实例
     *
     * @return
     */
    public synchronized static CoreData instance(String key) {
        CoreData instance = instanceMap.get(key);
        if (instance == null) {
            throw new IllegalStateException("CoreData has not been initialized, " +
                    "you must call CoreData.init(Application, Builder) when Application onCreate be invoked");
        }
        return instance;
    }

    /**
     * 注册序列化类型，此方法不调用也不会出问题，主要是为了保证Class的安全性
     * Serializable序列化默认不支持变更 Class的一切内容
     *
     * @param aClass
     */
    public static void registerSerializableClass(Class<? extends Serializable> aClass) {
        ObjectInputStreamWrap.registerClass(aClass);
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
                builder.password,
                builder.migrations,
                builder.tag);
        for (Map.Entry<Class, CoreDao> entry : coreDaoHashMap.entrySet()) {
            entry.getValue().onCreate(this);
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
