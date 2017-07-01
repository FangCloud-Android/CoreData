package com.coredata.compiler.utils;

import com.coredata.db.Property;
import com.coredata.utils.SqlUtils;

import java.util.List;

/**
 * Created by wangjinpeng on 2017/6/8.
 */

public class SqlBuilder {


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
            sql.append(SqlUtils.getSqlTypeByClazz(property.type));
            sql.append(property.primaryKey ? " PRIMARY KEY" : "");
            if (i < listSize - 1) {
                sql.append(",");
            }
        }
        sql.append(");");
        return sql.toString();
    }
}
