package com.frankzhou.project.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.frankzhou.project.annotation.FrequencyLimit;
import com.frankzhou.project.common.ResultCodeDTO;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.common.util.SpELUtil;
import com.frankzhou.project.model.common.FrequencyLimitDTO;
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
import java.util.concurrent.ConcurrentHashMap;
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
        FrequencyLimit[] annotationArray = method.getAnnotationsByType(FrequencyLimit.class);

        // key -> redis中的key value -> key对应的FrequencyLimit注解
        Map<String, FrequencyLimit> frequencyLimitMap = new ConcurrentHashMap<>();
        for (int i=0;i<annotationArray.length;i++) {
            FrequencyLimit item = annotationArray[i];
            String prefix = StringUtils.isNotBlank(item.prefixKey()) ? item.prefixKey() : SpELUtil.getMethodKey(method);
            String key = "";
            if (item.target().equals(FrequencyLimit.Target.UID)) {
                // 从token中获取
                key = "frankzhou";
            } else if (item.target().equals(FrequencyLimit.Target.IP)) {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                key = getHostIpAddress(request);
            } else if (item.target().equals(FrequencyLimit.Target.URL)) {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                key = getUrl(request);
            } else if (item.target().equals(FrequencyLimit.Target.EL)) {
                key = SpELUtil.parseSpEl(method,jp.getArgs(),item.spEl());
            }

            String redisKey = prefix + "index:" + i + ":" + key;
            frequencyLimitMap.put(redisKey, item);
        }

        List<String> keyList = new ArrayList<>(frequencyLimitMap.keySet());
        // List<Integer> countList = multiGetCache(keyList, Integer.class);
        List<Integer> countList = multiGetCount(keyList);
        // List<FrequencyLimitDTO> frequencyLimitDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(countList)) {
            for (int i=0;i< keyList.size();i++) {
                String key = keyList.get(i);
                FrequencyLimit frequencyLimit = frequencyLimitMap.get(key);
                // TODO 存在数组越界问题
                Integer limitCount = countList.get(i);
                if (ObjectUtil.isNotNull(limitCount) && limitCount >= frequencyLimit.count()) {
                    log.warn("用户{}调用{}方法次数过多，触发限流策略","frankzhou",SpELUtil.getMethodKey(method));
                    throw new BusinessException(new ResultCodeDTO(91131,"call api frequency limit","请求次数过多，请稍后再试"));
                }

                // FrequencyLimitDTO frequencyLimitDTO = buildFrequencyLimitDTO(key, frequencyLimit);
                // frequencyLimitDTOList.add(frequencyLimitDTO);
            }
        }

        try {
            return jp.proceed();
        } finally {
            // 自增调用次数
            frequencyLimitMap.forEach((k,v) -> {
                cacheIncr(k, v.time(), v.unit());
            });
        }
    }

    private <T> List<T> multiGetCache(List<String> keyList,Class<T> clazz) {
        List<String> cacheList = stringRedisTemplate.opsForValue().multiGet(keyList);
        if (CollectionUtils.isEmpty(cacheList) || Objects.isNull(cacheList)) {
            return new ArrayList<>();
        }

        return cacheList.stream().map(x -> BeanUtil.toBean((Object) x,clazz)).collect(Collectors.toList());
    }

    private List<Integer> multiGetCount(List<String> keyList) {
        List<String> cacheList = stringRedisTemplate.opsForValue().multiGet(keyList);
        if (cacheList == null || CollectionUtils.isEmpty(cacheList)) {
            return new ArrayList<>();
        }

        List<Integer> resultList = new ArrayList<>();
        for (String cache : cacheList) {
            if (ObjectUtil.isNotNull(cache)) {
                resultList.add(Integer.valueOf(cache));
            }
        }
        return resultList;
        // return cacheList.stream().map(x -> Integer.valueOf(x)).collect(Collectors.toList());
        // return cacheList.stream().map(Integer::valueOf).collect(Collectors.toList());
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

    private FrequencyLimitDTO buildFrequencyLimitDTO(String key, FrequencyLimit frequencyLimit) {
        FrequencyLimitDTO targetDto = FrequencyLimitDTO.builder()
                .key(key)
                .time(frequencyLimit.time())
                .unit(frequencyLimit.unit())
                .count(frequencyLimit.count())
                .build();
        return targetDto;
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
