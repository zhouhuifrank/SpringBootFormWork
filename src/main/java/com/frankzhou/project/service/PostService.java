package com.frankzhou.project.service;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.post.PostAddDTO;
import com.frankzhou.project.model.dto.post.PostQueryDTO;
import com.frankzhou.project.model.dto.post.PostUpdateDTO;
import com.frankzhou.project.model.vo.PostVO;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子管理接口
 * @date 2023-04-08
 */
public interface PostService {

    ResultDTO<Long> insertPost(PostAddDTO addRequest);

    ResultDTO<Boolean> deletePost(DeleteRequest deleteRequest);

    ResultDTO<Boolean> batchDeletePost(DeleteRequest deleteRequest);

    ResultDTO<Boolean> updatePost(PostUpdateDTO updateRequest);

    ResultDTO<PostVO> getPostById(PostQueryDTO queryRequest);

    ResultDTO<List<PostVO>> getPostList(PostQueryDTO queryRequest);

    PageResultDTO<List<PostVO>> getPostByPage(PostQueryDTO queryRequest);
}
