package com.frankzhou.project.aop;

import cn.hutool.core.bean.BeanUtil;
import com.frankzhou.project.annotation.FrequencyLimit;
import com.frankzhou.project.common.constant.ErrorConstant;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.common.util.SpELUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 接口调用频率控制切面
 * @date 2023-06-11
 */
@Slf4j
@Aspect
@Component
public class FrequencyLimitAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(com.frankzhou.project.annotation.FrequencyLimit)||@annotation(com.frankzhou.project.annotation.FrequencyLimitContainer)")
    public void aopPoint(){
    }

    @Around("aopPoint()")
    public Object doFrequencyLimit(ProceedingJoinPoint jp) throws Throwable {
        Method method = getMethod(jp);
        FrequencyLimit[] frequencyLimits = method.getAnnotationsByType(FrequencyLimit.class);
        // 可能有多个频控注解，遍历将key和对应注解存入map中
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Map<String,FrequencyLimit> annotationMap = new HashMap<>();
        for (int i=0;i<frequencyLimits.length;i++) {
            FrequencyLimit frequencyLimit = frequencyLimits[i];
            // 如果不设置prefix，那么就默认用方法全限定名 可能有多个注解prefix=方法全限定名+注解排名
            String prefix = StringUtils.isBlank(frequencyLimit.prefixKey()) ? SpELUtil.getMethodKey(method) + "index:" + i : frequencyLimit.prefixKey();
            String key = "";
            switch (frequencyLimit.target()) {
                case UID:
                    // 使用TokenUtil从token中解析出UID
                    break;
                case IP:
                    key = getHostIpAddress(request);
                    break;
                case URL:
                    key = getUrl(request);
                    break;
                case EL:
                    String springEl = frequencyLimit.spEl();
                    if (StringUtils.isNotBlank(springEl)) {
                        key = SpELUtil.parseSpEl(method,jp.getArgs(),springEl);
                    }
            }

            String redisKey = prefix;
            if (StringUtils.isNotBlank(key)) {
                redisKey += ":" + key;
            }
            annotationMap.put(redisKey,frequencyLimit);
        }

        // 批量获取redis中的统计值
        List<String> keyList = new ArrayList<>(annotationMap.keySet());
        List<Integer> countList = multiGetCache(keyList, Integer.class);
        for (int i=0;i<keyList.size();i++) {
            // 判断是否超过频率
            String key = keyList.get(i);
            Integer count = countList.get(i);
            FrequencyLimit frequencyLimit = annotationMap.get(key);
            if (Objects.nonNull(count) && count > frequencyLimit.count()) {
                // 频率超过了
                log.error("Frequency Limit key:{},value:{}",key,count);
                throw new BusinessException(ErrorConstant.FREQUENCY_LIMIT);
            }
        }

        try {
            return jp.proceed();
        } finally {
            // 自增统计值
            annotationMap.forEach((k,v) -> {
                cacheIncr(k,v.time(),v.unit());
            });
        }
    }

    private <T> List<T> multiGetCache(List<String> keyList,Class<T> clazz) {
        List<String> cacheList = stringRedisTemplate.opsForValue().multiGet(keyList);
        if (CollectionUtils.isEmpty(cacheList) || Objects.isNull(cacheList)) {
            return new ArrayList<>();
        }
        return cacheList.stream().map(x -> BeanUtil.toBean(x,clazz)).collect(Collectors.toList());
    }

    /**
     * 缓存自增，先判断key是否存在然后进行相应处理，最好把该方法执行步骤写成lua脚本保证原子性
     */
    private void cacheIncr(String key,long time,TimeUnit unit) {
        Boolean hasKey = stringRedisTemplate.hasKey(key);
        if (hasKey.equals(Boolean.TRUE)) {
            // key已经存在，则自增
            stringRedisTemplate.opsForValue().increment(key);
        } else {
            // key不存在，添加缓存
            stringRedisTemplate.opsForValue().setIfAbsent(key,"1",time,unit);
        }
    }

    private Method getMethod(ProceedingJoinPoint jp) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        return methodSignature.getMethod();
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

    private String getUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
