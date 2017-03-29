package com.wanpg.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 持久化注解
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Persistence {

    /**
     * 设置此参数的字段将被标记为主键
     * 一个class中只有一个字段可以被标记为主键
     *
     * @return
     */
    boolean primaryKey() default true;

    /**
     * 设置此参数且不为空的，持久化字段名将被设置
     * 如果未设置，字段名将与变量名保持一致
     *
     * @return
     */
    String name() default "";
}
