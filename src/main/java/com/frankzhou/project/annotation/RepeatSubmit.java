package com.frankzhou.project.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 防重复提交注解
 * @date 2023-06-03
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {

    /**
     * 防重复提交目标，默认为锁定ip，可选择用户id、ip地址、URL地址和方法全限定名
     */
    Target target() default Target.IP;

    /**
     * 防重提交间隔，默认为1
     */
    long interval() default 1;

    /**
     * 防重提交时间单位，默认为秒
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    enum Target {
        UID,IP,URL,METHOD;
    }
}
