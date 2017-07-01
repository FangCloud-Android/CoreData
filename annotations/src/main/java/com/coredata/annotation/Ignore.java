package com.coredata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于忽略字段，设置此注解的field，将不会被持久化
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Ignore {
}
