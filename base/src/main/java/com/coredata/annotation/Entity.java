package com.coredata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体类注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Entity {

    /**
     * 实体对应的表名，默认是实体{@link Class#getSimpleName()}
     *
     * @return tableName
     */
    String tableName() default "";

    /**
     * 这里指定当前表的主键列名，此处设置后会忽略{@link PrimaryKey}
     *
     * @return 主键列名
     */
    String primaryKey() default "";
}
