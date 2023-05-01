package com.frankzhou.project.mapper;

import com.frankzhou.project.model.entity.PostThumb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 22806
* @description 针对表【post_thumb(帖子点赞)】的数据库操作Mapper
* @createDate 2023-04-30 17:21:09
* @Entity com.frankzhou.project.model.entity.PostThumb
*/
@Mapper
public interface PostThumbMapper extends BaseMapper<PostThumb> {

}




