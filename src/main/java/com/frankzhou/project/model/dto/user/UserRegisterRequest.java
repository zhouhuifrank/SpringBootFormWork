package com.frankzhou.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户注册请求类
 * @date 2023-04-08
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userPassword;

    private String confirmPassword;

    private String phone;

    private String userRole;
}
