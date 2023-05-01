package com.frankzhou.project.service.impl;
import java.util.Date;
import java.util.ArrayList;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frankzhou.project.common.*;
import com.frankzhou.project.common.constant.OrderConstant;
import com.frankzhou.project.common.constant.UserRoleConstant;
import com.frankzhou.project.common.eunms.PostReviewStatusEnum;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.mapper.PostFavourMapper;
import com.frankzhou.project.mapper.PostMapper;
import com.frankzhou.project.mapper.PostThumbMapper;
import com.frankzhou.project.model.dto.post.PostAddDTO;
import com.frankzhou.project.model.dto.post.PostQueryDTO;
import com.frankzhou.project.model.dto.post.PostUpdateDTO;
import com.frankzhou.project.model.dto.user.UserDTO;
import com.frankzhou.project.model.dto.user.UserQueryRequest;
import com.frankzhou.project.model.entity.Post;
import com.frankzhou.project.model.entity.PostFavour;
import com.frankzhou.project.model.entity.PostThumb;
import com.frankzhou.project.model.vo.PostVO;
import com.frankzhou.project.model.vo.UserVO;
import com.frankzhou.project.service.PostService;
import com.frankzhou.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
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

    @Resource
    private PostThumbMapper thumbMapper;

    @Resource
    private PostFavourMapper favourMapper;

    @Override
    public ResultDTO<Long> insertPost(PostAddDTO addRequest) {
        if (ObjectUtil.isNull(addRequest)) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Post post = new Post();
        BeanUtil.copyProperties(addRequest,post);
        List<String> tags = addRequest.getTags();
        if (tags.size() > 0) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }

        ResultDTO<Boolean> validResult = this.validPost(post, true);
        if (validResult.getResultCode() != HttpStatus.HTTP_OK) {
            throw new BusinessException(validResult.getErrorMsg(),
                    validResult.getResultCode(),validResult.getError());
        }

        // 获取登录用户
        ResultDTO<UserDTO> loginResult = userService.getLoginUser();
        if (loginResult.getResultCode() != HttpStatus.HTTP_OK) {
            return ResultDTO.getErrorResult(ResultCodeConstant.USER_NOT_LOGIN);
        }
        UserDTO loginUser = loginResult.getData();
        if (ObjectUtil.isNotNull(loginUser)) {
            post.setUserId(loginUser.getId());
        } else {
            post.setUserId(1L);
        }
        post.setVisitNum(0);
        post.setThumbNum(0);
        post.setFavourNum(0);

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
        ResultDTO<UserDTO> loginResult = userService.getLoginUser();
        if (loginResult.getResultCode() != HttpStatus.HTTP_OK) {
            return ResultDTO.getErrorResult(ResultCodeConstant.USER_NOT_LOGIN);
        }
        UserDTO loginUser = loginResult.getData();
        if (!post.getUserId().equals(loginUser.getId()) || !loginUser.getRole().equals(UserRoleConstant.ADMIN_ROLE)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.NO_AUTH_ERROR);
        }

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
    public ResultDTO<Boolean> updatePost(PostUpdateDTO updateRequest) {
        if (ObjectUtil.isNull(updateRequest) || updateRequest.getId() <= 0) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        // 参数校验
        Post post = new Post();
        BeanUtil.copyProperties(updateRequest,post);
        List<String> tags = updateRequest.getTags();
        if (tags.size() > 0) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        ResultDTO<Boolean> validResult = this.validPost(post, false);
        if (validResult.getResultCode() != HttpStatus.HTTP_OK) {
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
        ResultDTO<UserDTO> loginResult = userService.getLoginUser();
        if (loginResult.getResultCode() != 200) {
            return ResultDTO.getErrorResult(ResultCodeConstant.USER_NOT_LOGIN);
        }
        UserDTO loginUser = loginResult.getData();
        if (!post.getUserId().equals(loginUser.getId()) || !loginUser.getRole().equals(UserRoleConstant.ADMIN_ROLE)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.NO_AUTH_ERROR);
        }

        Integer updateCount = postMapper.updateById(post);
        if (updateCount < 1) {
            return ResultDTO.getResult(ResultCodeConstant.DB_UPDATE_COUNT_ERROR);
        }
        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<PostVO> getPostById(PostQueryDTO queryRequest) {
        if (ObjectUtil.isNull(queryRequest) || queryRequest.getId() <= 0) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Post post = postMapper.selectById(queryRequest.getId());
        PostVO postVo = this.getPostVo(post);
        return ResultDTO.getSuccessResult(postVo);
    }

    @Override
    public ResultDTO<List<PostVO>> getPostList(PostQueryDTO queryRequest) {
        if (ObjectUtil.isNull(queryRequest)) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Post postQuery = new Post();
        BeanUtil.copyProperties(queryRequest,postQuery);
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>(postQuery);
        List<Post> postList = postMapper.selectList(postQueryWrapper);

        List<PostVO> postVOList = new ArrayList<>();
        postList.forEach(dto -> {
            PostVO postVo = this.getPostVo(dto);
            postVOList.add(postVo);
        });

        return ResultDTO.getSuccessResult(postVOList);
    }

    @Override
    public PageResultDTO<List<PostVO>> getPostByPage(PostQueryDTO queryRequest) {
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
            PostVO postVo = this.getPostVo(dto);
            postVOList.add(postVo);
        });

        return PageResultDTO.getSuccessPageResult(total,postVOList);
    }

    /**
     * 参数校验
     *
     * @author this.FrankZhou
     * @param post 帖子参数信息
     * @param isInsert 是否新增 true新增/false更新
     * @return true成功/false失败
     */
    private ResultDTO<Boolean> validPost(Post post,boolean isInsert) {
        if (ObjectUtil.isNull(post)) {
            return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        String title = post.getTitle();
        String content = post.getContent();
        String tags = post.getTags();

        if (isInsert) {
            // 如果是新增，那么这些参数都不能为空
            if (StringUtils.isAllBlank(title,content,tags)) {
                return ResultDTO.getResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
            }
        }

        // 更新时的参数校验
        if (StringUtils.isBlank(content) || content.length() > 8192) {
            return ResultDTO.getResult(new ResultCodeDTO(91132,"content is too long","内容过长"));
        }

        if (StringUtils.isBlank(title) || title.length() > 80) {
            return ResultDTO.getResult(new ResultCodeDTO(91132,"title is not null","标题过长"));
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    /**
     * 返回帖子封装类
     *
     * @author this.FrankZhou
     * @param post 帖子信息
     * @return postVo帖子封装信息
     */
    private PostVO getPostVo(Post post) {
        PostVO postVO = new PostVO();
        if (ObjectUtil.isNull(post)) {
            return postVO;
        }

        BeanUtil.copyProperties(post,postVO);
        String tags = post.getTags();
        if (StringUtils.isNotBlank(tags)) {
            // json数组转list
            JSONArray array = JSONUtil.parseArray(tags);
            List<String> tagList = JSONUtil.toList(array, String.class);
            postVO.setTags(tagList);
        }

        // 封装用户信息
        UserQueryRequest queryRequest = new UserQueryRequest();
        queryRequest.setId(post.getUserId());
        ResultDTO<UserVO> userById = userService.getById(queryRequest);
        if (userById.getResultCode() == HttpStatus.HTTP_OK) {
            postVO.setUser(userById.getData());
        }

        // 查询是否已经被点赞或者收藏(正在查看的用户)
        ResultDTO<UserDTO> loginUserResult = userService.getLoginUser();
        if (loginUserResult.getResultCode() == HttpStatus.HTTP_OK) {
            UserDTO user = loginUserResult.getData();
            Long userId = user.getId();
            // 查找该用户是否点赞
            LambdaQueryWrapper<PostThumb> thumbWrapper = new LambdaQueryWrapper<>();
            thumbWrapper.eq(PostThumb::getUserId,userId)
                    .eq(PostThumb::getPostId,post.getId());
            PostThumb postThumb = thumbMapper.selectOne(thumbWrapper);
            if (ObjectUtil.isNotNull(postThumb)) {
                postVO.setHasThumb(Boolean.TRUE);
            } else {
                postVO.setHasThumb(Boolean.FALSE);
            }

            // 查找该用户是否收藏
            LambdaQueryWrapper<PostFavour> favourWrapper = new LambdaQueryWrapper<>();
            favourWrapper.eq(PostFavour::getUserId,userId)
                    .eq(PostFavour::getPostId,post.getId());
            PostFavour postFavour = favourMapper.selectOne(favourWrapper);
            if (ObjectUtil.isNotNull(postFavour)) {
                postVO.setHashFavour(Boolean.TRUE);
            } else {
                postVO.setHashFavour(Boolean.FALSE);
            }
        }

        return postVO;
    }

    /**
     * 根据查询请求参数类构造queryWrapper
     *
     * @author this.FrankZhou
     * @param queryDTO 查询请求参数
     * @return queryWrapper
     */
    private QueryWrapper<Post> getQueryWrapper(PostQueryDTO queryDTO) {
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isNull(queryDTO)) {
            return wrapper;
        }

        // 两种情况，有分页参数和没有分页参数
        Long id = queryDTO.getId();
        String searchText = queryDTO.getSearchText();
        String title = queryDTO.getTitle();
        String content = queryDTO.getContent();
        List<String> tags = queryDTO.getTags();
        Long userId = queryDTO.getUserId();
        String orderBy = queryDTO.getOrderBy();
        String sort = queryDTO.getSort();
        // 分页参数为空，普通查询请求
        // searchText为搜索框中的参数，可能是标题也可能为帖子内容
        wrapper.like(StringUtils.isNotBlank(searchText),"title",searchText);
        wrapper.like(StringUtils.isNotBlank(searchText),"content",searchText);
        wrapper.like(StringUtils.isNotBlank(title),"title",title);
        wrapper.like(StringUtils.isNotBlank(content),"content",content);
        for (String tag : tags) {
            wrapper.like(StringUtils.isNotBlank(tag),"tag",","+ tag + ",");
        }
        wrapper.lambda().eq(Post::getId,id)
                .eq(Post::getUserId,userId);
        wrapper.lambda().eq(Post::getId,id)
                .eq(Post::getUserId,userId);
        wrapper.orderBy(StringUtils.isNotBlank(orderBy),sort.equals(OrderConstant.SORT_ORDER_ASC),orderBy);

        return wrapper;
    }
}
