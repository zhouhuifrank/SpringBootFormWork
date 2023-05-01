package com.frankzhou.project.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子新增请求类
 * @date 2023-04-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;

    private String content;

    private List<String> tags;
}
