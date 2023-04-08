package com.frankzhou.project.redis;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 分布式锁 使用线程标识解决锁的误删问题 使用lua脚本解决多条语句的原子性问题
 * @date 2023-01-26
 */
public class SimpleRedisLock implements RedisLock {

    private StringRedisTemplate stringRedisTemplate;

    private String name;

    private static final String DISTRIBUTED_LOCK = "distribution:key:";

    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean tryLock(Long timeSec) {
        String distributedKey = DISTRIBUTED_LOCK + name;
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(distributedKey, threadId, timeSec, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    @Override
    public void unlock() {
        // 调用lua脚本保证多条命令的原子性
        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(DISTRIBUTED_LOCK+name),
                ID_PREFIX + Thread.currentThread().getId());
    }

    /*
    @Override
    public void unlock() {
        String distributedKey = DISTRIBUTED_LOCK + name;
        String lock = stringRedisTemplate.opsForValue().get(distributedKey);
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        if (threadId.equals(lock)) {
            stringRedisTemplate.delete(distributedKey);
        }
    }
    */
}
