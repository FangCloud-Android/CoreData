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

    Class<?> converter();

    Class<?> dbType();
}
