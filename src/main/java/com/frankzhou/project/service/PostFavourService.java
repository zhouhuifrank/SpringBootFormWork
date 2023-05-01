package com.frankzhou.project.service;

import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.postFavour.PostFavourAddDTO;
import com.frankzhou.project.model.dto.postFavour.PostFavourQueryDTO;
import com.frankzhou.project.model.vo.PostVO;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 针对帖子收藏的业务逻辑操作
 * @date 2023-04-30
 */
public interface PostFavourService{

    /**
     * 帖子收藏/取消收藏
     *
     * @author this.FrankZhou
     * @param favourAddDTO 帖子收藏新增
     * @return true成功/false失败
     */
    ResultDTO<Boolean> doPostFavour(PostFavourAddDTO favourAddDTO) throws InterruptedException;

    /**
     * 封装内部的事务操作
     *
     * @author this.FrankZhou
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return true成功/false失败
     */
    ResultDTO<Boolean> doPostFavourTrans(Long postId, Long userId);

    /**
     * 分页查询我的帖子
     *
     * @author this.FrankZhou
     * @param favourQueryDTO 分页查询参数
     * @return 帖子集合
     */
    PageResultDTO<List<PostVO>> listMyFavourByPage(PostFavourQueryDTO favourQueryDTO);

    /**
     * 查询所有的帖子
     *
     * @author this.FrankZhou
     * @param favourQueryDTO 帖子查询参数
     * @return 帖子结合
     */
    ResultDTO<List<PostVO>> listAllFavourPost(PostFavourQueryDTO favourQueryDTO);

    /**
     * 分页查询某个用户帖子
     *
     * @author this.FrankZhou
     * @param favourQueryDTO 分页查询参数
     * @return 帖子集合
     */
    ResultDTO<List<PostVO>> listFavourByPage(PostFavourQueryDTO favourQueryDTO);
}
