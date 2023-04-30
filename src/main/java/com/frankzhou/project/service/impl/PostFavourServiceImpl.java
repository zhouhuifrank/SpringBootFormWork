package com.frankzhou.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankzhou.project.model.entity.PostFavour;
import com.frankzhou.project.service.PostFavourService;
import generator.mapper.PostFavourMapper;
import org.springframework.stereotype.Service;

/**
* @author 22806
* @description 针对表【post_favour(帖子收藏)】的数据库操作Service实现
* @createDate 2023-04-30 17:20:16
*/
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour>
    implements PostFavourService{

}




