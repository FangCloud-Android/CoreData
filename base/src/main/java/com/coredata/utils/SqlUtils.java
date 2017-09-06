package com.coredata.utils;

/**
 * sql的工具类
 */
public class SqlUtils {

    /**
     * 根据Class类型返回sqlite中对应的类型名称
     *
     * @param clazz 基础的类型的class，像String, int, long等
     * @return 数据库类型，像 text, int, bigint等
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

    /**
     * 返回删除表的sql
     *
     * @param tableName 表名
     * @return sql字符串
     */
    public static String getDropTableSql(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    public static String formatValue(Object object) {
        if (object == null) {
            return "NULL";
        } else if (object instanceof String) {
            return "'" + object.toString() + "'";

        } else if (object.getClass().equals(boolean.class)
                || object.getClass().equals(Boolean.class)) {
            if (Boolean.valueOf(object.toString())) {
                return "1";
            } else {
                return "0";
            }
        }
        return object.toString();
    }

    public static String formatColumnName(String columnName) {
        return "`" + columnName + "`";
    }
}
