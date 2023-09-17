package com.frankzhou.project.Util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-08-20
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class StringTests {

    @Test
    public void testSplit() {
        String url = "http://115.159.216.169:9200";

        int i = url.lastIndexOf("/");
        System.out.println(url.substring(i));
    }

    @Test
    public void testString2Integer() {
        String value = "null";
        Integer tableId = Integer.valueOf(value);
    }

    @Test
    public void testNull2String() {
        Long id = null;
        String idStr = String.valueOf(id);
        System.out.println(idStr);
        Integer tableId = Integer.valueOf(idStr);
    }

    @Test
    public void splitPath() {
        String treePath = "/1/2/3/4";
        int lastIndex = treePath.lastIndexOf("/");
        String substring = treePath.substring(0, lastIndex+1);
        System.out.println(substring);
    }
}
