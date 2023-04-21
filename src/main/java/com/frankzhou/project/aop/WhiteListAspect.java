package com.frankzhou.project.aop;

import com.frankzhou.project.annotation.WhiteList;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
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

    @Pointcut("@annotation(com.frankzhou.project.annotation.WhiteList)")
    public void aopPoint() {
    }

    @Around("aopPoint()")
    public Object doWhiteListInterceptor(ProceedingJoinPoint jp) throws Throwable {
        // 通过反射拿到注解信息
        Method method = getMethod(jp);
        WhiteList whiteList = method.getAnnotation(WhiteList.class);

        // 白名单校验

        Object result = jp.proceed();
        return result;
    }

    private Method getMethod(ProceedingJoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature signature =  (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(signature.getName(),signature.getParameterTypes());
    }
}
