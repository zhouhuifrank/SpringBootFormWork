package com.frankzhou.project.Util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-06-27
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CommonTests {

    @Test
    public void testFile() {
        String fileName = "unsolve.docx";
        int suffixIndex = fileName.lastIndexOf(".");
        String suffixName = "";
        if (suffixIndex > 0 && suffixIndex < fileName.length()-1) {
            suffixName = fileName.substring(suffixIndex+1);
        }

        String typeStr = "docx,doc,csv";
        if (typeStr.contains(suffixName)) {
            System.out.println("文件类型允许");
        }
        System.out.println(suffixIndex);
        System.out.println(suffixName);
    }
}
