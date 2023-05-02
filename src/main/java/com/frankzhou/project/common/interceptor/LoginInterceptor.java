package com.frankzhou.project.common.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.frankzhou.project.common.util.UserHolder;
import com.frankzhou.project.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 登录拦截器
 * @date 2023-04-22
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserVO UserVO = UserHolder.getUser();
        if (ObjectUtil.isNull(UserVO)) {
            // 用户信息不存在拦截
            log.info("用户不存在");
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            return false;
        }
        // 存在则放行
        return true;
    }
}
