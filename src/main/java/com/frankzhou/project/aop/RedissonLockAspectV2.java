package com.frankzhou.project.aop;

import com.frankzhou.project.annotation.RedissonLockV2;
import com.frankzhou.project.common.constant.ErrorConstant;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.common.util.SpELUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Redisson分布式锁切面版本2
 * @date 2023-06-10
 */
@Slf4j
@Aspect
@Component
@Order(0) // 分布式锁切面需要先于@Transcational执行
public class RedissonLockAspectV2 {

    @Resource
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.frankzhou.project.annotation.RedissonLockV2)")
    public void aopPoint() {
    }

    @Around("aopPoint()")
    public Object doRedissonLock(ProceedingJoinPoint jp) throws Throwable {
        Method method = getMethod(jp);
        RedissonLockV2 redissonLockV2 = method.getAnnotation(RedissonLockV2.class);
        String springEl = redissonLockV2.key();
        String prefix = StringUtils.isBlank(redissonLockV2.prefixKey()) ? SpELUtil.getMethodKey(method) : redissonLockV2.prefixKey();
        String key = SpELUtil.parseSpEl(method,jp.getArgs(),springEl);
        String redisKey = prefix + ":" + key;
        return executeLock(redisKey,
                redissonLockV2.waitTime(),
                redissonLockV2.unit(),
                jp::proceed);
    }

    private Method getMethod(ProceedingJoinPoint jp) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        return methodSignature.getMethod();
    }

    private <T> T executeLock(String lockKey, long waitTime, TimeUnit unit, SupplierThrow<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLockSuccess = lock.tryLock(waitTime, unit);
        if (!isLockSuccess) {
            throw new BusinessException(ErrorConstant.LOCK_LIMIT);
        }
        // 切面结束，执行方法
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 函数式接口Supplier生产者
     * 同Supplier提供get方法
     */
    @FunctionalInterface
    public interface SupplierThrow<T> {
        T get() throws Throwable;
    }
}
