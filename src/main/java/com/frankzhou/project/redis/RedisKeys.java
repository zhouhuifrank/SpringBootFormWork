package com.frankzhou.project.redis;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 存放redis的key和ttl过期时间常量
 * @date 2023-01-14
 */
public class RedisKeys {

    public static final String LOGIN_CODE_KEY = "login:code:";

    public static final Long LOGIN_CODE_TTL = 2L;

    public static final String LOGIN_USER_KEY = "login:user:";

    public static final Long LOGIN_USER_TTL = 30L;

    public static final String POST_THUMB_KEY = "post:thumb:";

    public static final String POST_FAVOUR_KEY = "post:favour:";
}
