package com.frankzhou.project.common.util;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description spring el表达式解析工具类
 * @date 2023-06-10
 */
public class SpELUtil {

    private static final ExpressionParser parser = new SpelExpressionParser();

    private static final DefaultParameterNameDiscoverer parameterDiscover = new DefaultParameterNameDiscoverer();

    public static String parseSpEl(Method method,Object[] args,String spEL) {
        // 获取方法参数名
        String[] parameterNames = parameterDiscover.getParameterNames(method);
        // 创建上下spEL上下文，将参数名和参数值填充进去
        EvaluationContext context = new StandardEvaluationContext();
        for (int i=0;i<parameterNames.length;i++) {
            context.setVariable(parameterNames[i],args[i]);
        }
        // 解析
        Expression expression = parser.parseExpression(spEL);
        String value = expression.getValue(context, String.class);
        return value;
    }

    public static String getMethodKey(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }
}
