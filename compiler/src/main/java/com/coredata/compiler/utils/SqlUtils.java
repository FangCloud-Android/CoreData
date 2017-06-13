package com.coredata.compiler.utils;

import com.coredata.compiler.db.Property;

import java.util.List;

/**
 * Created by wangjinpeng on 2017/6/8.
 */

public class SqlUtils {


    /**
     * 创建 建表的语句
     *
     * @param isNotExist
     * @return
     */
    public static String buildCreateSql(String tableName, List<Property> properties, boolean isNotExist) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(isNotExist ? "IF NOT EXISTS " : "")
                .append("'").append(tableName).append("' (");
        int listSize = properties.size();
        for (int i = 0; i < listSize; i++) {
            Property property = properties.get(i);
            sql.append("'").append(property.name).append("' ");
            sql.append(getSqlTypeByClazz(property.type));
            sql.append(property.primaryKey ? " PRIMARY KEY" : "");
            if (i < listSize - 1) {
                sql.append(",");
            }
        }
        sql.append(");");
        return sql.toString();
    }

    /**
     * 根据Class类型返回sqlite中对应的类型名称
     *
     * @param clazz
     * @return
     */
    public static String getSqlTypeByClazz(Class<?> clazz) {
        if (clazz == String.class) {
            return "TEXT";
        } else if (clazz == Integer.class) {
            return "INTEGER";
        } else if (clazz == Long.class || clazz == long.class) {
            return "BIGINT";
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return "INTEGER";
        } else if (clazz == int.class) {
            return "INT";
        }
        return "";
    }
}
