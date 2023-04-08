package com.frankzhou.project.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换相关操作
 */
public class DateUtil {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd hh:mm:ss";

    public static String getCurrentDate() {
        return formatDate(new Date(),DATE_PATTERN);
    }

    public static String getCurrentDateTime() {
        return formatDate(new Date(),DATE_TIME_PATTERN);
    }

    private static String formatDate(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        String newDate = dateFormat.format(date);
        return newDate;
    }
}
