package com.frankzhou.project.mapper;

import com.frankzhou.project.model.entity.PostFavour;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 22806
* @description 针对表【post_favour(帖子收藏)】的数据库操作Mapper
* @createDate 2023-04-30 17:20:16
* @Entity com.frankzhou.project.model.entity.PostFavour
*/
@Mapper
public interface PostFavourMapper extends BaseMapper<PostFavour> {

    Integer batchInsert(@Param("list") List<PostFavour> favourList);
}




