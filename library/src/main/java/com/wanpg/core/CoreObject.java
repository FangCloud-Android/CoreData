package com.wanpg.core;

import com.wanpg.core.annotation.Ignore;
import com.wanpg.core.annotation.Persistence;
import com.wanpg.core.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by wangjinpeng on 2017/3/21.
 * 对象数据类，基础的数据模型，也是持久化的桥梁
 */
public abstract class CoreObject {

    public CoreObject() {
        CoreData coreData = CoreData.defaultInstance();
        coreData.register(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * 是否已经从持久化里面删除此数据
     */
    @Ignore
    private boolean isPersistenceDeleted = false;

    public final boolean isPersistenceDeleted() {
        return isPersistenceDeleted;
    }

    /**
     * 获取主键值
     *
     * @return
     */
    public Object getPrimaryKey() {
        try {
            List<Field> allFields = ReflectUtils.getAllFields(getClass(), CoreObject.class);
            for (Field field : allFields) {
                Persistence annotationPer = field.getAnnotation(Persistence.class);
                if (annotationPer != null) {
                    if (annotationPer.primaryKey()) {
                        field.setAccessible(true);
                        return field.get(this);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("if you extends CoreObject, must set a primaryKey");
    }

    /**
     * 加载，会在缓存中读取数据，如果没有，会根据主键从持久化中实例化
     */
    public final void load() {
        if (isPersistenceDeleted()) {
            throw new IllegalStateException("current instance has been delete from db");
        }
    }

    /**
     * 刷新，将自己的数据刷入缓存和数据库，更新/插入
     */
    public final void refresh() {
        if (isPersistenceDeleted()) {
            throw new IllegalStateException("current instance has been delete from db");
        }
        CoreData.defaultInstance().refresh(this);
    }

    /**
     * 删除自己
     * 1、数据库移除自己
     * 2、缓存中标记{@link #isPersistenceDeleted}标记
     * 3、从缓存中移除自己
     */
    public final void delete() {
        isPersistenceDeleted = true;
    }

    /**
     * 复制数据, 将每一个数据都进行复制
     *
     * @param t
     */
    public final void _update(CoreObject t) {
        if (this == t) {
            return;
        }
        List<Field> allFields = ReflectUtils.getAllFields(getClass(), CoreObject.class);
        for (Field field : allFields) {
            Ignore annotation = field.getAnnotation(Ignore.class);
            if (annotation == null) {
                try {
                    field.setAccessible(true);
                    field.set(this, field.get(t));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        this.isPersistenceDeleted = t.isPersistenceDeleted;
    }
}
