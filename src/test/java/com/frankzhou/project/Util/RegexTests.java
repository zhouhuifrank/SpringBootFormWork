package com.frankzhou.project.Util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-09-05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RegexTests {

    @Test
    public void testNumberRegex() {
        String tableId = "null";
        String regex = "\\d+";

        if (tableId.matches(regex)) {
            System.out.println("正则表达式匹配");
        } else {
            System.out.println("正则表达式不匹配");
        }
    }

}
