package com.coredata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键的注解，目前只支持单一主键
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface PrimaryKey {
}
