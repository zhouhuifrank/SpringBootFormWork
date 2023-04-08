package com.frankzhou.project.common.util;

/**
 * 字符串格式化工具类
 */
public class StringFormatUtil {

    public static String toUpperFirstChar(String str) {
        char[] charArray = str.toCharArray();
        int num = (int) charArray[0];
        num = num - 32;
        char resChar = (char) num;
        charArray[0] = resChar;
        String resString = String.valueOf(charArray);
        return resString;
    }

    public static String toLowerFirstChar(String str) {
        char[] charArray = str.toCharArray();
        int num = (int) charArray[0];
        num = num + 32;
        char resChar = (char) num;
        charArray[0] = resChar;
        String resString = String.valueOf(charArray);
        return resString;
    }

}
