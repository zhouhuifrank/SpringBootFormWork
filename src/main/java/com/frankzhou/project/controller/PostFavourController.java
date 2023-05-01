package com.frankzhou.project.controller;

import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.postFavour.PostFavourAddDTO;
import com.frankzhou.project.model.dto.postFavour.PostFavourQueryDTO;
import com.frankzhou.project.model.vo.PostVO;
import com.frankzhou.project.service.PostFavourService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子收藏控制器
 * @date 2023-04-30
 */
@Slf4j
@RestController
@Api(tags = {"帖子收藏管理"})
@RequestMapping("/postFavour")
public class PostFavourController {

    @Resource
    private PostFavourService postFavourService;

    // TODO 需要解决当前登录用户查询出来为空的情况
    @PostMapping("/")
    public ResultDTO<Boolean> doFavour(@RequestBody PostFavourAddDTO favourAddDTO) throws InterruptedException {
        return postFavourService.doPostFavour(favourAddDTO);
    }

    @PostMapping("/my/list/page")
    public ResultDTO<List<PostVO>> listMyFavourByPage(@RequestBody PostFavourQueryDTO favourQueryDTO) {
        return postFavourService.listMyFavourByPage(favourQueryDTO);
    }


    @PostMapping("/list/page")
    public ResultDTO<List<PostVO>> listFavourByPage(@RequestBody PostFavourQueryDTO favourQueryDTO) {
        return postFavourService.listFavourByPage(favourQueryDTO);
    }
}
