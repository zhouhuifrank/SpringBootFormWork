package com.frankzhou.project.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子更新请求参数
 * @date 2023-04-08
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateDTO {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String content;

    private List<String> tags;
}
