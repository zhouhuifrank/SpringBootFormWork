package com.frankzhou.project.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 接口访问频率限制注解
 * @date 2023-06-03
 */
@Documented
@Repeatable(FrequencyLimitContainer.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FrequencyLimit {

    /**
     * key的前缀，默认为方法全类型，一般不需要指定，除非在不同的方法对统一资源进行频率限制
     */
    String prefixKey() default "";

    /**
     * 频率限制对象，支持UID,IP,URL和SpEL表达式
     */
    Target target() default Target.IP;

    /**
     * springEL表达式 如果Target=EL，则必填
     */
    String spEl() default "";

    /**
     * 频率限制时间单位
     */
    long time();

    /**
     * 频率时间单位，默认为秒
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 在单位时间内能够发送请求的次数
     */
    int count();

    enum Target {
        UID,IP,URL,EL
    }
}
