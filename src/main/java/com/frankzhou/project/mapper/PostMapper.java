package com.frankzhou.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.project.model.dto.post.PostQueryDTO;
import com.frankzhou.project.model.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子数据处理层
 * @date 2023-04-08
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    List<Post> queryPostByCond(PostQueryDTO queryDTO);

    List<Post> queryPostById(@Param("list") List<Long> postIdList);

    Integer addPostThumb(@Param("postId") Long postId,@Param("addNum") Integer addNum);

    Integer addPostFavour(@Param("postId") Long postId,@Param("addNum") Integer addNum);
}
