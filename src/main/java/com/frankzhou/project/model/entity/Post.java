package com.frankzhou.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子实体类
 * @date 2023-04-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "post")
public class Post implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "age")
    private Integer age;

    @TableField(value = "gender")
    private Integer gender;

    @TableField(value = "education")
    private String education;

    @TableField(value = "place")
    private String place;

    @TableField(value = "job")
    private String job;

    @TableField(value = "contact")
    private String contact;

    @TableField(value = "love_experience")
    private String loveExperience;

    @TableField(value = "content")
    private String content;

    @TableField(value = "photo")
    private String photo;

    @TableField(value = "review_status")
    private Integer reviewStatus;

    @TableField(value = "review_message")
    private String reviewMessage;

    @TableField(value = "view_num")
    private Integer viewNum;

    @TableField(value = "collect_num")
    private Integer collectNum;

    @TableField(value = "thumb_num")
    private Integer thumbNum;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}
