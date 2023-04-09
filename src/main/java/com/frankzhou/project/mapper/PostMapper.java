package com.frankzhou.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.project.model.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子数据处理层
 * @date 2023-04-08
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
