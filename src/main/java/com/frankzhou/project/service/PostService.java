package com.frankzhou.project.service;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.post.PostAddRequest;
import com.frankzhou.project.model.dto.post.PostQueryRequest;
import com.frankzhou.project.model.dto.post.PostUpdateRequest;
import com.frankzhou.project.model.vo.PostVO;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子管理接口
 * @date 2023-04-08
 */
public interface PostService {

    ResultDTO<Long> insertPost(PostAddRequest addRequest);

    ResultDTO<Boolean> deletePost(DeleteRequest deleteRequest);

    ResultDTO<Boolean> batchDeletePost(DeleteRequest deleteRequest);

    ResultDTO<Boolean> updatePost(PostUpdateRequest updateRequest);

    ResultDTO<PostVO> getPostById(PostQueryRequest queryRequest);

    ResultDTO<List<PostVO>> getPostList(PostQueryRequest queryRequest);

    PageResultDTO<List<PostVO>> getPostByPage(PostQueryRequest queryRequest);
}
