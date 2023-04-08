package com.frankzhou.project.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description RedisId生成器
 * @date 2023-01-18
 */
@Component
public class RedisIdGenerator {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    private static final long BASE_TIMESTAMP = 1674071231;

    private static final long COUNT_BITS = 32;

    /**
     * 通过redis自增长功能得到全局唯一id  1位符号位+31位时间戳+32位序列号
     * key使用incr:keyPrefix+date 每天都不是不一样得key，保证id生成器得可用性
     *
     * @author this.FrankZhou
     * @param keyPrefix key的前缀
     * @return id 生成的id
     */
    public long nextId(String keyPrefix) {
        // 31位时间戳+32位redis自增increment
        LocalDateTime now = LocalDateTime.now();
        long currentSeconds = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = currentSeconds - BASE_TIMESTAMP;

        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = stringRedisTemplate.opsForValue().increment("incr:"+keyPrefix+date);

        // 位运算 左移32位然后或运算拼接
        return timeStamp << COUNT_BITS | count;
    }

}
