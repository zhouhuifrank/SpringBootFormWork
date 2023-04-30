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
 * @description 帖子实体类
 * @date 2023-04-08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "post")
public class Post implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "title")
    private String title;

    @TableField(value = "content")
    private String content;

    @TableField(value = "tags")
    private String tags;

    @TableField(value = "visit_num")
    private Integer visitNum;

    @TableField(value = "thumb_num")
    private Integer thumbNum;

    @TableField(value = "favour_num")
    private Integer favourNum;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}
