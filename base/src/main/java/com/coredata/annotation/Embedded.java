package com.coredata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 内嵌类注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Embedded {
}
