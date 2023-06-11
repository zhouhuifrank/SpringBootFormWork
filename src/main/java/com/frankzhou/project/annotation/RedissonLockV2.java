package com.frankzhou.project.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 分布式锁注解 spEL表达式版本
 * @date 2023-06-10
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLockV2 {

    /**
     * key的前缀，默认为方法全限定名，一般不需要设置，除非需要在不同方法中锁定同一种资源，那么可以手动设置
     */
    String prefixKey() default "";

    /**
     * springEL表达式 可在运行时解析出入参值
     */
    String key();

    /**
     * 获取锁的等待时间，默认-1不等待
     */
    long waitTime() default -1;

    /**
     * 时间单位，默认毫秒
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
