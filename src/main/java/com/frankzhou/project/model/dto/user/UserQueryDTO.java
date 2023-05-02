package com.frankzhou.project.model.dto.user;

import com.frankzhou.project.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户查询请求类
 * @date 2023-04-08
 */
@Data
public class UserQueryDTO extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String userName;

    private String userAccount;

    private String gender;

    private String phone;

    private String email;

    private String userRole;
}
