package com.frankzhou.project.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户前端显示类
 * @date 2023-04-08
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String userName;

    private String phone;

    private String role;

}
