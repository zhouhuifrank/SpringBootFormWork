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

    /**
     * 接口入参需要提取的属性
     */
    String key() default "";

    /**
     * 拦截时返回给用户的json
     */
    String returnJson() default "";
}
