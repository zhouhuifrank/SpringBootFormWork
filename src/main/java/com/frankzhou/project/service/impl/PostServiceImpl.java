package com.frankzhou.project.service.impl;
import java.util.ArrayList;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frankzhou.project.common.*;
import com.frankzhou.project.common.constant.OrderConstant;
import com.frankzhou.project.common.eunms.PostGenderStatusEnum;
import com.frankzhou.project.common.eunms.PostReviewStatusEnum;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.common.util.RegexUtils;
import com.frankzhou.project.mapper.PostMapper;
import com.frankzhou.project.model.dto.post.PostAddRequest;
import com.frankzhou.project.model.dto.post.PostQueryRequest;
import com.frankzhou.project.model.dto.post.PostUpdateRequest;
import com.frankzhou.project.model.entity.Post;
import com.frankzhou.project.model.vo.PostVO;
import com.frankzhou.project.service.PostService;
import com.frankzhou.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-04-08
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserService userService;

    @Override
    public ResultDTO<Long> insertPost(PostAddRequest addRequest) {
        if (ObjectUtil.isNull(addRequest)) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Post post = new Post();
        BeanUtil.copyProperties(addRequest,post);
        ResultDTO<Boolean> validResult = this.validPost(post, true);
        if (validResult.getResultCode() != 200) {
            throw new BusinessException(validResult.getErrorMsg(),
                    validResult.getResultCode(),validResult.getError());
        }

        // 获取登录用户
        post.setUserId(1L);

        // 新增的时候必须为待审核状态
        if (!post.getReviewStatus().equals(PostReviewStatusEnum.VERIFY_BEFORE.getCode())) {
            post.setReviewStatus(PostReviewStatusEnum.VERIFY_BEFORE.getCode());
        }

        Integer insertCount = postMapper.insert(post);
        if (insertCount < 1) {
            return ResultDTO.getResult(ResultCodeConstant.DB_INSERT_COUNT_ERROR);
        }
        Long postId = post.getId();
        return ResultDTO.getSuccessResult(postId);
    }

    @Override
    public ResultDTO<Boolean> deletePost(DeleteRequest deleteRequest) {
        if (ObjectUtil.isNull(deleteRequest) || deleteRequest.getId() <= 0) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Long postId = deleteRequest.getId();
        Post post = postMapper.selectById(postId);
        if (ObjectUtil.isNull(post)) {
            return ResultDTO.getResult(new ResultCodeDTO(91123,"post is not exist","帖子不存在"));
        }

        // 只有管理员和自己才能删除帖子

        Integer deleteCount = postMapper.deleteById(postId);
        if (deleteCount < 1) {
            return ResultDTO.getResult(ResultCodeConstant.DB_DELETE_COUNT_ERROR);
        }
        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> batchDeletePost(DeleteRequest deleteRequest) {
        if (ObjectUtil.isNull(deleteRequest) || StringUtils.isBlank(deleteRequest.getIds())) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        String ids = deleteRequest.getIds();
        String[] idStrList = ids.split(",");
        List<Long> idList = new ArrayList<>();
        for (int i=0;i<idStrList.length;i++) {
            idList.add(Long.valueOf(idStrList[i]));
        }
        Integer deleteCount = postMapper.deleteBatchIds(idList);
        if (deleteCount != idList.size()) {
            return ResultDTO.getResult(ResultCodeConstant.DB_DELETE_COUNT_ERROR);
        }
        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> updatePost(PostUpdateRequest updateRequest) {
        if (ObjectUtil.isNull(updateRequest) || updateRequest.getId() <= 0) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        // 参数校验
        Post post = new Post();
        BeanUtil.copyProperties(updateRequest,post);
        ResultDTO<Boolean> validResult = this.validPost(post, false);
        if (validResult.getResultCode() != 200) {
            throw new BusinessException(validResult.getErrorMsg(),
                    validResult.getResultCode(),validResult.getError());
        }

        // 判断是否存在，存在则更新
        Long postId = post.getId();
        Post oldPost = postMapper.selectById(postId);
        if (ObjectUtil.isNull(oldPost)) {
            return ResultDTO.getResult(new ResultCodeDTO(91134,"post is not exist","帖子不存在"));
        }

        // 只有管理员和作者本人可以更新

        Integer updateCount = postMapper.updateById(post);
        if (updateCount < 1) {
            return ResultDTO.getResult(ResultCodeConstant.DB_UPDATE_COUNT_ERROR);
        }
        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<PostVO> getPostById(PostQueryRequest queryRequest) {
        if (ObjectUtil.isNull(queryRequest) || queryRequest.getId() <= 0) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Post post = postMapper.selectById(queryRequest.getId());
        PostVO postVO = new PostVO();
        BeanUtil.copyProperties(post,postVO);
        return ResultDTO.getSuccessResult(postVO);
    }

    @Override
    public ResultDTO<List<PostVO>> getPostList(PostQueryRequest queryRequest) {
        if (ObjectUtil.isNull(queryRequest)) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Post postQuery = new Post();
        BeanUtil.copyProperties(queryRequest,postQuery);
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>(postQuery);
        List<Post> postList = postMapper.selectList(postQueryWrapper);

        List<PostVO> postVOList = new ArrayList<>();
        postList.forEach(dto -> {
            PostVO vo = new PostVO();
            BeanUtil.copyProperties(dto,vo);
            postVOList.add(vo);
        });

        return ResultDTO.getSuccessResult(postVOList);
    }

    @Override
    public PageResultDTO<List<PostVO>> getPostByPage(PostQueryRequest queryRequest) {
        if (ObjectUtil.isNull(queryRequest)) {
            return PageResultDTO.getErrorPageResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Post postQuery = new Post();
        BeanUtil.copyProperties(queryRequest,postQuery);
        // 支持content的模糊搜索
        postQuery.setContent(null);
        Integer currPage = queryRequest.getCurrPage();
        Integer pageSize = queryRequest.getPageSize();
        String sort = queryRequest.getSort();
        String orderBy = queryRequest.getOrderBy();
        String content = queryRequest.getContent();

        // 默认值
        if (ObjectUtil.isNull(currPage)) {
            currPage = 1;
        }

        if (ObjectUtil.isNull(pageSize)) {
            pageSize = 10;
        }

        if (StringUtils.isBlank(sort)) {
            sort = OrderConstant.SORT_ORDER_ASC;
        }

        if (StringUtils.isBlank(orderBy)) {
            orderBy = "update_time";
        }

        // 防止爬虫
        if (pageSize > 100) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>(postQuery);
        postQueryWrapper.orderBy(StringUtils.isNotBlank(orderBy),
                sort.equals(OrderConstant.SORT_ORDER_ASC),orderBy);
        postQueryWrapper.lambda().like(StringUtils.isNotBlank(content),Post::getContent,content);
        Page<Post> postPage = postMapper.selectPage(new Page<>(currPage, pageSize), postQueryWrapper);

        List<Post> records = postPage.getRecords();
        int total = (int) postPage.getTotal();
        List<PostVO> postVOList = new ArrayList<>();
        records.forEach(dto -> {
            PostVO vo = new PostVO();
            BeanUtil.copyProperties(dto,vo);
            postVOList.add(vo);
        });

        return PageResultDTO.getSuccessPageResult(total,postVOList);
    }

    private ResultDTO<Boolean> validPost(Post post,boolean isInsert) {
        if (ObjectUtil.isNull(post)) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Integer age = post.getAge();
        Integer gender = post.getGender();
        String place = post.getPlace();
        String job = post.getJob();
        String contact = post.getContact();
        String loveExperience = post.getLoveExperience();
        String content = post.getContent();
        Integer reviewStatus = post.getReviewStatus();

        if (isInsert) {
            // 如果是新增，那么这些参数都不能为空
            if (StringUtils.isAllBlank(place,job,contact,loveExperience,content) || ObjectUtils.anyNull(age,gender,reviewStatus)) {
                return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
            }
        }

        // 更新时的参数校验
        if (ObjectUtil.isNull(gender) || !PostGenderStatusEnum.getValues().contains(gender)) {
            return ResultDTO.getResult(new ResultCodeDTO(91132,"gender is invalid","性别不符合要求"));
        }

        if (ObjectUtil.isNull(content) || content.length() > 8192) {
            return ResultDTO.getResult(new ResultCodeDTO(91132,"conten is too long","内容过长"));
        }

        if (ObjectUtil.isNull(reviewStatus) || !PostReviewStatusEnum.getValues().contains(reviewStatus)) {
            return ResultDTO.getResult(new ResultCodeDTO(91132,"review status is invalid","审核状态非法"));
        }

        if (ObjectUtil.isNull(contact) || RegexUtils.phoneIsInvalid(contact)) {
            return ResultDTO.getResult(new ResultCodeDTO(91132,"phone is invalid","手机号不合法"));
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }
}
