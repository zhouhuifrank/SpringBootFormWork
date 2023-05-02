package com.frankzhou.project.common.util;

import com.frankzhou.project.model.vo.UserVO;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 保存登录的用户信息
 * @date 2023-04-09
 */
public class UserHolder {
    private static final ThreadLocal<UserVO> tl = new ThreadLocal<>();

    public static UserVO getUser() {
        UserVO UserVO = tl.get();
        return UserVO;
    }

    public static void setUser(UserVO user) {
        tl.set(user);
    }

    public static void remove() {
        tl.remove();
    }
}
