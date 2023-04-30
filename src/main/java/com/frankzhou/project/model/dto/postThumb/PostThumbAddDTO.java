package com.frankzhou.project.model.dto.postThumb;

import lombok.Data;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子点赞请求类
 * @date 2023-04-30
 */
@Data
public class PostThumbAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long postId;
}
