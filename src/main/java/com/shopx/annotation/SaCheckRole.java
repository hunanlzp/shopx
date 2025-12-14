package com.shopx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色校验注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaCheckRole {
    
    /**
     * 需要校验的角色标识
     */
    String[] value() default {};
    
    /**
     * 角色模式
     */
    String mode() default "AND";
    
    /**
     * 角色类型
     */
    String type() default "";
}
