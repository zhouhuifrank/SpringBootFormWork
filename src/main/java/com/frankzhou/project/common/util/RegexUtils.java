package com.frankzhou.project.common.util;


import com.alibaba.druid.util.StringUtils;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 正则表达式校验工具类
 * @date 2023-01-14
 */
public class RegexUtils {

    /**
     * 校验手机号是否符合规则
     *
     * @author this.FrankZhou
     * @param phone 用户手机号
     * @return boolean true->符合正则表达式/false->不符合正则表达式
     */
    public static boolean phoneIsInvalid(String phone) {
        return !isMatch(phone,RegexPatterns.PHONE_REGEX);
    }

    /**
     * 校验邮箱是否符合规则
     *
     * @author this.FrankZhou
     * @param email 用户邮箱
     * @return boolean true->符合正则表达式/false->不符合正则表达式
     */
    public static boolean emailIsInvalid(String email) {
        return !isMatch(email,RegexPatterns.EMAIL_REGEX);
    }

    /**
     * 校验密码是否符合规则
     *
     * @author this.FrankZhou
     * @param password 用户手机号
     * @return boolean true->符合正则表达式/false->不符合正则表达式
     */
    public static boolean passwordIsInvalid(String password) {
        return !isMatch(password,RegexPatterns.PASSWORD_REGEX);
    }

    /**
     * 校验验证码是否符合规则
     *
     * @author this.FrankZhou
     * @param code 用户手机号
     * @return boolean true->符合正则表达式/false->不符合正则表达式
     */
    public static boolean codeIsInvalid(String code) {
        return !isMatch(code,RegexPatterns.VERIFY_CODE_REGEX);
    }

    /**
     * 判断是否符合正则表达式
     *
     * @author this.FrankZhou
     * @param info 需要校验的信息
     * @param regexPattern 正则表达式
     * @return boolean true->符合正则表达式/false->不符合正则表达式
     */
    public static boolean isMatch(String info,String regexPattern) {
        if (StringUtils.isEmpty(info)) {
            return true;
        }

        return info.matches(regexPattern);
    }
}
