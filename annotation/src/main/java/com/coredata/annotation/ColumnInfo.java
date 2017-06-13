package com.coredata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 纵列信息注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface ColumnInfo {

    /**
     * 设置此参数且不为空的，持久化字段名将被设置
     * 如果未设置，字段名将与变量名保持一致
     *
     * @return
     */
    String name() default "";

}
