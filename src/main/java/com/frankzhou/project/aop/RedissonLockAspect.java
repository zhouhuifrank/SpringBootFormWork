package com.frankzhou.project.aop;

import com.frankzhou.project.annotation.RedissonLock;
import com.frankzhou.project.common.constant.ErrorConstant;
import com.frankzhou.project.common.exception.BusinessException;
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
 * @description Redisson分布式锁注解
 * @date 2023-06-09
 */
@Slf4j
@Aspect
@Component
@Order(0) // 分布式锁注解必须先于@Transcational注解执行，分布式锁中包含事务操作
public class RedissonLockAspect {

    @Resource
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.frankzhou.project.annotation.RedissonLock)")
    public void aopPoint() {
    }

    @Around("aopPoint()")
    public Object doRedissonLockInterceptor(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Method method = methodSignature.getMethod();
        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
        String fullName = getMethodFullName(method);
        String prefix = redissonLock.prefixKey();
        String key = redissonLock.key();

        log.info("方法的全限定名为:{}",fullName);
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isBlank(prefix)) {
            sb.append(fullName);
        } else {
            sb.append(prefix);
        }
        if (StringUtils.isNotBlank(key)) {
            sb.append(":");
            sb.append(key);
        }
        String redisKey = sb.toString();
        log.info("分布式锁键为:{}",redisKey);
        return executeLock(jp,redisKey,redissonLock.waitTime(),redissonLock.unit());
    }

    private Object executeLock(ProceedingJoinPoint jp,String redisKey, long waitTime, TimeUnit unit) throws Throwable {
        RLock lock = redissonClient.getLock(redisKey);
        log.info("分布式锁等待时间:{}{}",waitTime,unit);
        boolean lockSuccess = lock.tryLock(waitTime, unit);
        if (!lockSuccess) {
            throw new BusinessException(ErrorConstant.LOCK_LIMIT);
        }
        log.info("分布式锁获取成功:{}",redisKey);
        try {
            return jp.proceed();
        } finally {
            lock.unlock();
        }
    }

    private String getMethodFullName(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }
}
