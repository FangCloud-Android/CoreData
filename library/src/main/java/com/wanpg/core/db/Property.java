package com.wanpg.core.db;

/**
 * Created by Shayabean on 2016/5/24.
 * 存储table字段
 */
public class Property {

    public final String name;
    public final Class<?> type;
    public final boolean primaryKey;

    public Property(String name, Class<?> type, boolean primaryKey) {
        this.type = type;
        this.primaryKey = primaryKey;
        this.name = name;
    }

    /**
     * 注意：只比较ordinal、type、name
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Property
                && this.type == ((Property) o).type
                && this.name.equals(((Property) o).name); // 排除大小写干扰
    }

    /**
     * @return 列名
     */
    @Override
    public String toString() {
        return name;
    }
}
