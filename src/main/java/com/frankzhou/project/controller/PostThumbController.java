package com.frankzhou.project.controller;

import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.postThumb.PostThumbAddDTO;
import com.frankzhou.project.model.entity.PostThumb;
import com.frankzhou.project.service.PostThumbService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子点赞控制器
 * @date 2023-04-30
 */
@Slf4j
@RestController
@Api(tags = {"帖子点赞管理"})
@RequestMapping("/postThumb")
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @ApiOperation(value = "帖子点赞/取消点赞",notes = "硬删除实现取消点赞")
    @PostMapping("/")
    public ResultDTO<Boolean> doThumb(@RequestBody PostThumbAddDTO thumbAddDTO) {
        return postThumbService.doPostThumb(thumbAddDTO);
    }
}
