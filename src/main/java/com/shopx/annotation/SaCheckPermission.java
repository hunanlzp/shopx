package com.shopx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaCheckPermission {
    
    /**
     * 需要校验的权限码
     */
    String[] value() default {};
    
    /**
     * 权限码模式
     */
    String mode() default "AND";
    
    /**
     * 权限码类型
     */
    String type() default "";
}
