package com.frankzhou.project.redis;

import cn.hutool.core.util.BooleanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 缓存工具类 互斥锁 设置逻辑过期时间 解决缓存击穿问题 使用函数式编程以及泛型来实现用户的自定义过程
 * @date 2023-01-14
 */
@Slf4j
@Component
public class RedisCache {

    private StringRedisTemplate stringRedisTemplate;

    private final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    public RedisCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public <T> void setObject(String key, T value, Long time, TimeUnit unit) {
        return;
    }

    public <T> void setWithRandomTime(String key, T value, Long time, TimeUnit unit) {

    }

    public <T> void setNullCache(String key, T value, Long time, TimeUnit unit) {

    }

    public <T> void setWithLogicTime(String key, T value, Long time, TimeUnit unit) {
        return;
    }

    public <T, ID> T queryWithPenetrate(String key, ID id, T value, Class<T> clazz,
                                         Function<ID, T> dbCallBack, Long time, TimeUnit unit) {

        return null;
    }

    public <T, ID> T queryWithMutex(String key, ID id, T value, Class<T> clazz,
                                     Function<ID, T> dbCallBack, Long time, TimeUnit unit) {
        return null;
    }

    public <T, ID> T queryWithLogicTime(String key, ID id, T value, Class<T> clazz,
                                         Function<ID, T> dbCallBack, Long time, TimeUnit unit) {
        return null;
    }

    /**
     * 获取互斥锁并设置锁的过期时间
     *
     * @author this.FrankZhou
     * @param key 锁的key
     * @param time 锁的过期时间
     * @param unit 时间单位 秒/分钟/小时
     * @return true->获取锁成功/false->获取锁失败
     */
    public boolean tryLock(String key, Long time, TimeUnit unit) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key,"1",time,unit);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放互斥锁
     *
     * @author this.Frankzhou
     * @param key 锁的key
     * @return void
     */
    public void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

}
