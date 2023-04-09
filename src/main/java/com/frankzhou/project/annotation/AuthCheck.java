package com.frankzhou.project.annotation;

import java.lang.annotation.*;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 权限校验注解
 * @date 2023-04-09
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    String[] anyRole() default "";

    String mustRole() default "";
}
