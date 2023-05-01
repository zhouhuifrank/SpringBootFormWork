package com.frankzhou.project.service;

import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.postThumb.PostThumbAddDTO;
import com.frankzhou.project.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 针对帖子点赞的业务逻辑操作
 * @date 2023-04-30
 */
public interface PostThumbService {

    /**
     * 帖子点赞/取消点赞
     *
     * @author this.FrankZhou
     * @param thumbAddDTO 点赞新增
     * @return true成功/false失败
     */
    ResultDTO<Boolean> doPostThumb(PostThumbAddDTO thumbAddDTO);

    /**
     * 封装了内部的事务操作
     *
     * @author this.FrankZhou
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return true成功/false失败
     */
    ResultDTO<Boolean> doPostThumbTrans(Long postId,Long userId);
}
