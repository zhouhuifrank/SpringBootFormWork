package com.frankzhou.project.model.dto.post;

import com.frankzhou.project.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子查询请求参数
 * @date 2023-04-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostQueryDTO extends PageRequest {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String searchTest;

    private List<String> tags;

    private List<String> orTags;

    private Long userId;

    private Long favourUserId;
}
