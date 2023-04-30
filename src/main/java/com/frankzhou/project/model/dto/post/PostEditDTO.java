package com.frankzhou.project.model.dto.post;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子编辑请求类
 * @date 2023-04-30
 */
@Data
public class PostEditDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String content;

    private List<String> tags;
}
