package com.frankzhou.project.common.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.DigestUtils;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 密码加密验证 MD5+盐
 * @date 2023-01-20
 */
public class PasswordEncoder {

    public static String encode(String password) {
        if (StrUtil.isBlank(password)) {
            return null;
        }
        // 20位的随机盐
        String salt = RandomUtil.randomString(20);

        return encode(salt,password);
    }

    public static String encode(String salt,String password) {
        String encodePassword = salt + "@" + DigestUtils.md5DigestAsHex((password+salt).getBytes());

        return encodePassword;
    }

    public static Boolean isMatch(String rawPassword,String encodePassword) {
        if (StrUtil.isEmpty(rawPassword) || StrUtil.isEmpty(encodePassword)) {
            return Boolean.FALSE;
        }

        if (!encodePassword.contains("@")) {
            return Boolean.FALSE;
        }

        // 从@salt的标记处分割出加密后的密码
        String[] charSet = encodePassword.split("@");
        String salt = charSet[0];
        if (encode(salt,rawPassword).equals(encodePassword)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

}
