package com.coredata.core;

/**
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
     * 注意：只比较type、name
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Property
                && this.type == ((Property) o).type
                && this.name.equals(((Property) o).name);
    }

    /**
     * @return 列名
     */
    @Override
    public String toString() {
        return name;
    }
}
