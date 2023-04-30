package com.frankzhou.project.model.dto.postFavour;

import lombok.Data;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子收藏请求参数
 * @date 2023-04-30
 */
@Data
public class PostFavourAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long postId;
}
