package com.frankzhou.project.controller;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.post.PostAddRequest;
import com.frankzhou.project.model.dto.post.PostQueryRequest;
import com.frankzhou.project.model.dto.post.PostUpdateRequest;
import com.frankzhou.project.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子前端控制器
 * @date 2023-04-08
 */
@Slf4j
@RestController
@Api(tags = {"帖子管理"})
@RequestMapping("/post")
public class PostController {

    @Resource
    private PostService postService;

    @ApiOperation(value = "【新增】单个新增帖子",notes = "单个帖子新增")
    @PostMapping("/add")
    public ResultDTO<Boolean> addPost(PostAddRequest addRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "【删除】单个删除帖子",notes = "单个帖子删除")
    @PostMapping("/delete")
    public ResultDTO<Boolean> deletePost(DeleteRequest deleteRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "【删除】批量删除帖子",notes = "批量帖子删除")
    @PostMapping("/batchDelete")
    public ResultDTO<Boolean> batchDeletePost(DeleteRequest deleteRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "【更新】单个更新帖子",notes = "单个帖子更新")
    @PostMapping("/update")
    public ResultDTO<Boolean> updatePost(PostUpdateRequest updateRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "【查询】单个查询帖子",notes = "单个帖子查询")
    @PostMapping("/get")
    public ResultDTO<Boolean> getPost(PostQueryRequest queryRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "【查询】批量查询帖子",notes = "批量帖子查询")
    @PostMapping("/list")
    public ResultDTO<Boolean> getPostList(PostQueryRequest queryRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "【查询】分页查询帖子",notes = "分页帖子查询")
    @PostMapping("/list/page")
    public ResultDTO<Boolean> getPostByPage(PostQueryRequest queryRequest) {
        return ResultDTO.getSuccessResult();
    }
}
