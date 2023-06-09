package com.frankzhou.project.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 删除请求类
 * @date 2023-04-08
 */
@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 单条数据删除
     */
    private Long id;

    /**
     * 多条数据删除 已逗号隔开
     */
    private String ids;
}
