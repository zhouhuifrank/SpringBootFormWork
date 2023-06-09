package com.frankzhou.project.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 分布式锁注解
 * @date 2023-06-04
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {

    /**
     * key的前缀，默认取方法的全限定名，除非需要在不同方法上锁定同一个资源可以自己指定，一般不需要指定
     */
    String prefixKey() default "";

    /**
     * key的后缀，可实现一些特殊的资源锁定需求
     */
    String key() default "";

    /**
     * 获取分布式锁的等待时间，默认为-1不等待，Redisson默认的waiteTime也是-1
     */
    long waitTime() default -1L;

    /**
     * 时间单位，可自己设定，默认为毫秒
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
