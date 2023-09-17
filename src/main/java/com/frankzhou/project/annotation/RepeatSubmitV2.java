package com.frankzhou.project.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 表单防重提交注解 spEL表达式版本
 * @date 2023-06-10
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmitV2 {

    /**
     * key的前缀，默认为方法全限定名
     */
    String prefixKey() default "";

    /**
     * 锁定资源标识，默认锁定UID
     */
    Target target() default Target.UID;

    /**
     * springEL表达式
     */
    String springEl();

    /**
     * 防止重复提交的间隔，默认为1
     */
    long interval() default 1;

    /**
     * 时间单位，默认为秒
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    enum Target {
        UID,IP,URL,EL
    }
}
