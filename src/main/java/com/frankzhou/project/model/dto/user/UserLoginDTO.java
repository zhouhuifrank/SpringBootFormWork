package com.frankzhou.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户登录请求类 支持用户名&密码登录/支持手机号&验证码登录
 * @date 2023-04-08
 */
@Data
public class UserLoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userPassword;

    private String phone;

    private String code;
}
