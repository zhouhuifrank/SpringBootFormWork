package com.frankzhou.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 帖子点赞
 * @TableName post_thumb
 */
@TableName(value ="post_thumb")
@Data
public class PostThumb implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 帖子 id
     */
    @TableField(value = "postId")
    private Long postid;

    /**
     * 创建用户 id
     */
    @TableField(value = "userId")
    private Long userid;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createtime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updatetime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}