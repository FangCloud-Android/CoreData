package com.coredata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据转换器
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Convert {

    /**
     * 转换器Class
     *
     * @return 实现了com.coredata.core.PropertyConverter的Class
     */
    Class<?> converter();

    /**
     * 制定对象在数据库中的类型
     *
     * @return 数据库中的类型
     */
    Class<?> dbType();
}
