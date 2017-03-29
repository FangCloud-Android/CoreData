package com.wanpg.core.db;

import android.text.TextUtils;

import com.wanpg.core.CoreObject;
import com.wanpg.core.annotation.Ignore;
import com.wanpg.core.annotation.Persistence;
import com.wanpg.core.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SqlBuilder {


    /*
     *
     * 备注
     * getType() 获取field的类型
     * getGenericType()
     * getDeclaringClass() 获取field的声明处，返回定义它的class
     * class1.isAssignableFrom(class2) 判断class1是否与class2相同或者是class2的父类
     * 无参构造函数始终会会被最先执行
     *
     */


    /**
     * 创建 建表的语句
     *
     * @param isNotExist
     * @return
     */
    public static String buildCreateSql(Class<? extends CoreObject> aClass, boolean isNotExist) {
        String tableName = null;
        Persistence annotation = aClass.getAnnotation(Persistence.class);
        if (annotation != null) {
            tableName = annotation.name();
        }
        if (TextUtils.isEmpty(tableName)) {
            tableName = aClass.getSimpleName();
        }
        List<Property> listCreatePro = new ArrayList<>();
        List<Field> allFields = ReflectUtils.getAllFields(aClass, CoreObject.class);
        for (Field field : allFields) {
            Ignore annotationIgnore = field.getAnnotation(Ignore.class);
            if (annotationIgnore != null) {
                continue;
            }
            Persistence annotationPer = field.getAnnotation(Persistence.class);
            String name = null;
            boolean primaryKey = false;
            if (annotationPer != null) {
                name = annotationPer.name();
                primaryKey = annotationPer.primaryKey();
            }
            if (TextUtils.isEmpty(name)) {
                name = field.getName();
            }
            Class<?> type = field.getType();
            if (!isBaseType(type)) {
                // 判断CoreObject是否是field的父类
                if (CoreObject.class.isAssignableFrom(type)) {
                    // 存储相应主键
                    // // FIXME: 2017/3/21 此处需要存储关联对象的主键
                    type = String.class;
                } else {
                    // 存储字符串
                    type = String.class;
                }
            }
            Property property = new Property(name, type, primaryKey);
            listCreatePro.add(property);
        }

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(isNotExist ? "IF NOT EXISTS " : "")
                .append("'").append(tableName).append("' (");
        int listSize = listCreatePro.size();
        for (int i = 0; i < listSize; i++) {
            Property property = listCreatePro.get(i);
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

    private static List<Class<?>> baseTypeList = new ArrayList<>();

    static {

        baseTypeList.add(byte.class);
        baseTypeList.add(Byte.class);

        baseTypeList.add(short.class);
        baseTypeList.add(Short.class);

        baseTypeList.add(int.class);
        baseTypeList.add(Integer.class);

        baseTypeList.add(long.class);
        baseTypeList.add(Long.class);

        baseTypeList.add(double.class);
        baseTypeList.add(Double.class);

        baseTypeList.add(float.class);
        baseTypeList.add(Float.class);

        baseTypeList.add(boolean.class);
        baseTypeList.add(Boolean.class);

        baseTypeList.add(char.class);

        baseTypeList.add(String.class);
    }

    /**
     * 判断是否是基本类型
     *
     * @param type
     * @return
     */
    private static boolean isBaseType(Class<?> type) {
        return baseTypeList.contains(type);
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
        } else if (clazz == Long.class) {
            return "BIGINT";
        } else if (clazz == Boolean.class) {
            return "INTEGER";
        } else if (clazz == int.class) {
            return "INT";
        }
        return "";
    }
}