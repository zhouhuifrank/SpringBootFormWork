package com.frankzhou.project.redis;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Redis分布式锁基础版
 * @date 2023-01-26
 */
public interface RedisLock {

    /**
     * 尝试获取锁
     *
     * @author this.FrankZhou
     * @param timeSec 锁的有效时间
     * @return boolean true->获取锁成功/false->获取锁失败
     */
    boolean tryLock(Long timeSec);

    /**
     * 释放分布式锁
     *
     * @author this.FrankZhou
     * @return void
     */
    void unlock();
}
