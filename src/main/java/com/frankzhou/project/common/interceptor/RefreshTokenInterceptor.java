package com.frankzhou.project.common.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.frankzhou.project.common.util.UserHolder;
import com.frankzhou.project.model.vo.UserVO;
import com.frankzhou.project.redis.RedisKeys;
import com.frankzhou.project.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description token刷新拦截器
 * @date 2023-04-22
 */
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {

    /**
     * 拦截器没有被spring管理，所以需要通过构造函数注入
     */
    private RedisUtil redisUtil;

    public RefreshTokenInterceptor(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if (StringUtils.isBlank(token)) {
            log.info("token不存在,请重新登录");
            return true;
        }
        String loginUserKey = RedisKeys.LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = redisUtil.getCacheHash(loginUserKey);
        if (ObjectUtil.isNull(userMap)) {
            log.info("用户信息不存在");
            return true;
        }

        // 存入ThreadLocal中
        UserVO UserVO = BeanUtil.fillBeanWithMap(userMap, new UserVO(), false);
        UserHolder.setUser(UserVO);

        // 刷新token
        boolean isSuccess = redisUtil.setExpire(loginUserKey, RedisKeys.LOGIN_USER_TTL, TimeUnit.MINUTES);
        if (!isSuccess) {
            return false;
        }
        log.info("刷新token成功");

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 把用户信息从ThreadLoacl中删除
        UserHolder.remove();
    }
}
