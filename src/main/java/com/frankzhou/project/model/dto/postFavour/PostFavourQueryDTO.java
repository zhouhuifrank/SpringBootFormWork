package com.frankzhou.project.model.dto.postFavour;

import com.frankzhou.project.common.PageRequest;
import com.frankzhou.project.model.dto.post.PostQueryDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子收藏请求类
 * @date 2023-04-30
 */
@Data
public class PostFavourQueryDTO extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private PostQueryDTO postQueryDTO;

    private Long userId;
}
