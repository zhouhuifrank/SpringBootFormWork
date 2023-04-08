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

    public static final String CACHE_SHOP_KEY = "cache:shop:";

    public static final Long CACHE_SHOP_TTL = 30L;

    public static final String CACHE_SHOP_TYPE_KEY = "cache:shop:type:";

    public static final String CACHE_NULL_KEY = "cache:null:";

    public static final Long CACHE_NULL_TTL = 30L;

    public static final String MUTEX_LOCK_KEY = "mutex:lock:";

    public static final Long MUTEX_LOCK_TTL = 10L;

    public static final String VOUCHER_STOCK_KEY = "voucher:stock:";

    public static final String VOUCHER_LIST_KEY = "voucher:list:";
}
