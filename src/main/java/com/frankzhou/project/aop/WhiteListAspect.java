package com.frankzhou.project.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.frankzhou.project.annotation.WhiteList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 白名单切面
 * @date 2023-04-09
 */
@Slf4j
@Aspect
@Component
public class WhiteListAspect {

    @Value("${whitelist.users}")
    private String users;

    @Pointcut("@annotation(com.frankzhou.project.annotation.WhiteList)")
    public void aopPoint() {
    }

    @Around("aopPoint()")
    public Object doWhiteListInterceptor(ProceedingJoinPoint jp) throws Throwable {
        // 通过反射拿到注解信息
        Method method = getMethod(jp);
        String methodName = method.getName();
        WhiteList whiteList = method.getAnnotation(WhiteList.class);

        // 获取字段信息
        String fieldKey = whiteList.key();
        String fieldValue = getFieldValue(fieldKey, jp.getArgs());
        log.info("白名单拦截方法:{},字段值:{}",methodName,fieldValue);
        if (StringUtils.isBlank(fieldValue)) {
            return jp.proceed();
        }

        // 白名单校验
        String[] userList = users.split(",");
        for (String user : userList) {
            if (fieldValue.equals(user)) {
                // 白名单匹配放行
                return jp.proceed();
            }
        }

        // 根据returnJson设置的值返回给用户
        // TODO 如果拦截是不是直接抛出异常比较好
        return returnResult(method,whiteList);
    }

    private Method getMethod(ProceedingJoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature signature =  (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(signature.getName(),signature.getParameterTypes());
    }

    private Method getMethodV2(ProceedingJoinPoint jp) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Method method = methodSignature.getMethod();
        return method;
    }

    private String getFieldValue(String field, Object[] args) {
        String fieldValue = null;
        // 遍历每一个入参
        for (Object arg : args) {
            try {
                if (null == fieldValue || StringUtils.isBlank(fieldValue)) {
                    fieldValue = BeanUtil.getProperty(arg,fieldValue);
                } else {
                    break;
                }
            } catch (Exception e) {
                if (args.length == 1) {
                    fieldValue = args[0].toString();
                }
            }
        }

        return fieldValue;
    }

    private Object returnResult(Method method,WhiteList whiteList) throws InstantiationException, IllegalAccessException {
        Class<?> returnType = method.getReturnType();
        String returnJson = whiteList.returnJson();
        if (StringUtils.isBlank(returnJson)) {
            return returnType.newInstance();
        }

        Object result = JSONUtil.toBean(returnJson, returnType);
        return result;
    }
}
