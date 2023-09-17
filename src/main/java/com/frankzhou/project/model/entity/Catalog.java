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
 * @description
 * @date 2023-09-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("catalog")
public class Catalog implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "parent_id")
    private Long parentId;

    @TableField(value = "catalog_name")
    private String catalogName;

    @TableField(value = "level")
    private Integer level;

    @TableField(value = "tree_path")
    private String treePath;

    @TableField(value = "sort_num")
    private Integer sortNum;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}
