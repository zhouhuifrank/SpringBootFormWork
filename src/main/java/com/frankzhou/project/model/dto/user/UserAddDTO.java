package com.frankzhou.project.model.dto.user;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户新增请求类
 * @date 2023-04-08
 */
public class UserAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;

    private String userAccount;

    private String userAvatar;

    private String gender;

    private String phone;

    private String email;

    private String userRole;

    private String userPassword;
}
