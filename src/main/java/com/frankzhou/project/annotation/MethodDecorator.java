package com.frankzhou.project.annotation;

import java.lang.annotation.*;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 方法装饰器注解
 * @date 2023-06-04
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodDecorator {

    /**
     * 可以配置单个方法
     */
    String method() default "";

    /**
     * 可以配置多个方法
     */
    String[] methodList() default "";

    /**
     * 拦截时返回的信息
     */
    String returnJson() default "";
}
