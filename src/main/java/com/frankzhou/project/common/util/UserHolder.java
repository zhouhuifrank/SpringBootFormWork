package com.frankzhou.project.common.util;

import com.frankzhou.project.model.dto.user.UserDTO;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 保存登录的用户信息
 * @date 2023-04-09
 */
public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static UserDTO getUser() {
        UserDTO userDTO = tl.get();
        return userDTO;
    }

    public static void setUser(UserDTO user) {
        tl.set(user);
    }

    public static void remove() {
        tl.remove();
    }
}
