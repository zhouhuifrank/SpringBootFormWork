package com.frankzhou.project.Util;

import com.frankzhou.project.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author This.FrankZhou
 * @version 1.0
 * @description spEL表达式测试
 * @date 2023-06-10
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringElTests {

    @Test
    public void testParseEL() {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        User user = new User();
        user.setUserAccount("zhouhui");
        List<String> list = Arrays.asList("a","b");
        Map<String,String> map = new HashMap<>();
        map.put("A","zhou");
        map.put("B","hui");
        context.setVariable("user",user);
        context.setVariable("list",list);
        context.setVariable("map",map);

        String account = parser.parseExpression("#user.getUserAccount").getValue(context, String.class);
        String listValue = parser.parseExpression("#list[1]").getValue(context, String.class);
        String mapValue = parser.parseExpression("#map[B]").getValue(context,String.class);
        log.info("user account:{},list value:{},map value:{}",account,listValue,mapValue);
    }
}
