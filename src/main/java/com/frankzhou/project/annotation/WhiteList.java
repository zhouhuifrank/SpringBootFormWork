package com.frankzhou.project.annotation;

import java.lang.annotation.*;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户白名单
 * @date 2023-04-09
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WhiteList {

    String userId() default "";
}
