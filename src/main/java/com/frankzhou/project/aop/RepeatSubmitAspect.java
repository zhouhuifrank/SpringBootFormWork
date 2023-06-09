package com.frankzhou.project.aop;

import com.frankzhou.project.annotation.RepeatSubmit;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.redis.StringRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 表单防重提交
 * @date 2023-06-09
 */
@Slf4j
@Aspect
@Component
public class RepeatSubmitAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(com.frankzhou.project.annotation.RepeatSubmit)")

    public void aopPoint(){
    }

    @Around("aopPoint()")
    public Object doRepeatSubmitInterceptor(ProceedingJoinPoint jp) throws Throwable {
        // 获取注解
        Method method = getMethod(jp);
        RepeatSubmit repeatSubmit = method.getAnnotation(RepeatSubmit.class);
        RepeatSubmit.Target target = repeatSubmit.target();

        log.info("锁定对象:{}",target);
        // 从ThreadLocal中拿到Request对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 获取参数进行转换
        String repeatKey = "";
        if (target.equals(RepeatSubmit.Target.IP)) {
            repeatKey = getHostIpAddress(request);
        } else if (target.equals(RepeatSubmit.Target.UID)) {
            // TODO 需要从Token中获取
        } else if (target.equals(RepeatSubmit.Target.METHOD)) {
            repeatKey = getMethodFullName(method);
        } else if (target.equals(RepeatSubmit.Target.URL)) {
            repeatKey = getUrl(request);
        }
        log.info("重复提交key:{}",repeatKey);

        long intervalTime = repeatSubmit.interval();
        TimeUnit unit = repeatSubmit.unit();
        return executeInspect(jp,repeatKey,intervalTime,unit);
    }

    private Object executeInspect(ProceedingJoinPoint jp,String redisKey,long time,TimeUnit unit) throws Throwable {
        String jsonValue = stringRedisTemplate.opsForValue().get(redisKey);
        // 缓存存在，出现重复提交情况，抛出异常
        if (jsonValue != null) {
            throw new BusinessException("请勿重复提交");
        }
        // 在限定时间范围内第一次提交，设置缓存
        log.info("用户首次提交");
        stringRedisTemplate.opsForValue().set(redisKey,"1",time,unit);
        // 继续执行方法提交表单
        return jp.proceed();
    }

    private Method getMethod(ProceedingJoinPoint jp) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        return methodSignature.getMethod();
    }

    private String getMethodFullName(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }

    private String getHostIpAddress(HttpServletRequest request) {
        // 从多个请求头中获取ip
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        // 获取本低ip
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        if (ipAddress != null && ipAddress.length() > 15 && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        return ipAddress;
    }

    private String getHostIp(HttpServletRequest request) {
        String remoteHost = request.getRemoteHost();
        int remotePort = request.getRemotePort();
        return remoteHost + remotePort;
    }

    private String getUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
