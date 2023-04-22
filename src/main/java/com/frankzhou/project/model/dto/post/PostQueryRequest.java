package com.frankzhou.project.model.dto.post;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.frankzhou.project.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子查询请求参数
 * @date 2023-04-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostQueryRequest extends PageRequest {

    private Long id;

    private String place;

    private String job;

    private String loveExperience;

    private String content;

    private Integer reviewStatus;

    private Long userId;
}
