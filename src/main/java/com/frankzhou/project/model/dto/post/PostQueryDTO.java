package com.frankzhou.project.model.dto.post;

import com.frankzhou.project.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
public class PostQueryDTO extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     *  ES搜索关键词
     */
    private String searchText;

    private String title;

    private String content;

    private List<String> tags;

    private Long userId;

    private Long favourUserId;
}
