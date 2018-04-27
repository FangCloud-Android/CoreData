package com.coredata.db;

/**
 * 存储table字段,type指的是db的数据类型，比如 INT, BIGINT, TEXT等
 */
public class DbProperty {

    public final String name;
    public final String type;
    public final boolean primaryKey;

    public DbProperty(String name, String type, boolean primaryKey) {
        this.type = type;
        this.primaryKey = primaryKey;
        this.name = name;
    }

    /**
     * 全部比较，主键也纳入比对范围，当主键不一致，也说明是不同的列
     *
     * @param o 给定参数
     * @return true 为相等，false为不相等
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof DbProperty
                && this.type.equals(((DbProperty) o).type)
                && this.name.equals(((DbProperty) o).name)
                && this.primaryKey == ((DbProperty) o).primaryKey;
    }

    /**
     * @return 列名
     */
    @Override
    public String toString() {
        return name;
    }
}
