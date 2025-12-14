package com.shopx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API版本注解
 * 用于标记API版本
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    
    /**
     * API版本号
     */
    String value() default "v1";
    
    /**
     * 是否废弃
     */
    boolean deprecated() default false;
    
    /**
     * 废弃说明
     */
    String deprecationReason() default "";
}
