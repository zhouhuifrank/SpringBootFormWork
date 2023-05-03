package com.frankzhou.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户实体类
 * @date 2023-04-08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user")
public class User implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "user_account")
    private String userAccount;

    @TableField(value = "user_avatar")
    private String userAvatar;

    @TableField(value = "gender")
    private String gender;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "email")
    private String email;

    @TableField(value = "user_role")
    private String userRole;

    @TableField(value = "user_password")
    private String userPassword;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}
