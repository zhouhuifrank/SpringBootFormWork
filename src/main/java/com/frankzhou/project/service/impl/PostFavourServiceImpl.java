package com.frankzhou.project.service.impl;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.constant.OrderConstant;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.project.common.ResultCodeConstant;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.mapper.PostFavourMapper;
import com.frankzhou.project.mapper.PostMapper;
import com.frankzhou.project.model.dto.postFavour.PostFavourAddDTO;
import com.frankzhou.project.model.dto.postFavour.PostFavourQueryDTO;
import com.frankzhou.project.model.entity.Post;
import com.frankzhou.project.model.entity.PostFavour;
import com.frankzhou.project.model.vo.PostVO;
import com.frankzhou.project.model.vo.UserVO;
import com.frankzhou.project.redis.RedisKeys;
import com.frankzhou.project.redis.StringRedisUtil;
import com.frankzhou.project.service.PostFavourService;
import com.frankzhou.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 针对帖子收藏的业务逻辑操作实现
 * @date 2023-04-30
 */
@Slf4j
@Aspect
@Service
public class PostFavourServiceImpl implements PostFavourService {

    @Resource
    private UserService userService;

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostFavourMapper favourMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisUtil stringRedisUtil;

    @Override
    public ResultDTO<Boolean> doPostFavour(PostFavourAddDTO favourAddDTO) {
        if (ObjectUtil.isNull(favourAddDTO) || favourAddDTO.getPostId() <= 0) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        ResultDTO<UserVO> loginUserResult = userService.getLoginUser();
        if (loginUserResult.getResultCode() != HttpStatus.HTTP_OK) {
            return ResultDTO.getErrorResult(ResultCodeConstant.USER_NOT_LOGIN);
        }
        UserVO user = loginUserResult.getData();
        Long userId = user.getId();
        Long postId = favourAddDTO.getPostId();

        // 使用分布式锁或者synchronized
        String lockKey = RedisKeys.POST_FAVOUR_KEY + userId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean flag = lock.tryLock(10, TimeUnit.SECONDS);
            if (flag) {
                // 获取代理对象执行事务方法
                PostFavourService proxy = (PostFavourService) AopContext.currentProxy();
                return proxy.doPostFavourTrans(postId,userId);
            } else {
                log.info("Redisson分布式锁获取失败，请稍后重试");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        return ResultDTO.getErrorResult(ResultCodeConstant.DISTRIBUTED_LOCK_FAIL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> doPostFavourTrans(Long postId, Long userId) {
        LambdaQueryWrapper<PostFavour> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostFavour::getPostId,postId)
                .eq(PostFavour::getUserId,userId);
        PostFavour oldPostFavour = favourMapper.selectOne(queryWrapper);
        Integer updateCount;
        if (ObjectUtil.isNull(oldPostFavour)) {
            // 收藏
            PostFavour postFavour = new PostFavour();
            postFavour.setPostId(postId);
            postFavour.setUserId(userId);
            int insertCount = favourMapper.insert(postFavour);
            if (insertCount < 1) {
                throw new BusinessException(ResultCodeConstant.DB_INSERT_COUNT_ERROR);
            }

            updateCount = postMapper.addPostFavour(postId, 1);
        } else {
            // 取消收藏
            int deleteCount = favourMapper.deleteById(oldPostFavour.getId());
            if (deleteCount < 1) {
                throw new BusinessException(ResultCodeConstant.DB_DELETE_COUNT_ERROR);
            }

            updateCount = postMapper.addPostFavour(postId, -1);
        }

        if (updateCount < 1) {
            throw new BusinessException(ResultCodeConstant.DB_UPDATE_COUNT_ERROR);
        }
        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public PageResultDTO<List<PostVO>> listMyFavourByPage(PostFavourQueryDTO favourQueryDTO) {
        if (ObjectUtil.isNull(favourQueryDTO) || favourQueryDTO.getUserId() <= 0) {
            return PageResultDTO.getErrorPageResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Integer currPage = favourQueryDTO.getCurrPage();
        Integer pageSize = favourQueryDTO.getPageSize();
        String orderBy = favourQueryDTO.getOrderBy();
        String sort = favourQueryDTO.getSort();
        if (ObjectUtil.isNull(currPage)) {
            currPage = 1;
        }
        if (ObjectUtil.isNull(pageSize)) {
            pageSize = 10;
        }
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "update_time";
        }
        if (StringUtils.isBlank(sort)) {
            sort = OrderConstant.SORT_ORDER_DESC;
        }

        // 防止爬虫
        if (pageSize > 100) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        // 分页搜索出userId所收藏的帖子
        ResultDTO<UserVO> loginUserResult = userService.getLoginUser();
        if (loginUserResult.getResultCode() != HttpStatus.HTTP_OK) {
            return PageResultDTO.getErrorPageResult(ResultCodeConstant.USER_NOT_LOGIN);
        }
        UserVO user = loginUserResult.getData();
        Long userId = user.getId();

        QueryWrapper<PostFavour> wrapper = new QueryWrapper<>();
        wrapper.orderBy(StringUtils.isNotBlank(orderBy),sort.equals(OrderConstant.SORT_ORDER_ASC),orderBy);
        wrapper.lambda().eq(PostFavour::getUserId,userId);
        Page<PostFavour> listByPage = favourMapper.selectPage(new Page<>(currPage, pageSize), wrapper);

        if (listByPage.getTotal() == 0) {
            // 如果没有数据那么给前端返回空数组
            List<PostVO> emptyList = new ArrayList<>();
            return PageResultDTO.getSuccessPageResult(0,emptyList);
        }

        List<PostFavour> records = listByPage.getRecords();
        int total = (int) listByPage.getTotal();
        List<Long> postIdList = records.stream().map(PostFavour::getPostId).collect(Collectors.toList());
        // 根据postId查到所有的帖子信息
        List<Post> postList = postMapper.queryPostById(postIdList);
        List<PostVO> postVOList = new ArrayList<>();
        postList.forEach(dto -> {
            PostVO vo = new PostVO();
            BeanUtil.copyProperties(dto,vo);
            postVOList.add(vo);
        });

        return PageResultDTO.getSuccessPageResult(postVOList.size(),postVOList);
    }

    @Override
    public ResultDTO<List<PostVO>> listAllFavourPost(PostFavourQueryDTO favourQueryDTO) {
        if (ObjectUtil.isNull(favourQueryDTO)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        List<PostVO> postVOList = new ArrayList<>();
        Long userId = favourQueryDTO.getUserId();
        LambdaQueryWrapper<PostFavour> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavour::getUserId,userId);
        List<PostFavour> postFavourList = favourMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(postFavourList)) {
            return ResultDTO.getSuccessResult(postVOList);
        }

        List<Long> postIdList = postFavourList.stream().map(PostFavour::getPostId).collect(Collectors.toList());
        List<Post> postList = postMapper.queryPostById(postIdList);
        postList.forEach(dto -> {
            PostVO vo = new PostVO();
            BeanUtil.copyProperties(dto,vo);
            postVOList.add(vo);
        });

        return ResultDTO.getSuccessResult(postVOList);
    }

    @Override
    public ResultDTO<List<PostVO>> listFavourByPage(PostFavourQueryDTO favourQueryDTO) {
        if (ObjectUtil.isNull(favourQueryDTO) || favourQueryDTO.getUserId() <= 0) {
            return PageResultDTO.getErrorPageResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Integer currPage = favourQueryDTO.getCurrPage();
        Integer pageSize = favourQueryDTO.getPageSize();
        String orderBy = favourQueryDTO.getOrderBy();
        String sort = favourQueryDTO.getSort();
        if (ObjectUtil.isNull(currPage)) {
            currPage = 1;
        }
        if (ObjectUtil.isNull(pageSize)) {
            pageSize = 10;
        }
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "update_time";
        }
        if (StringUtils.isBlank(sort)) {
            sort = OrderConstant.SORT_ORDER_DESC;
        }

        // 防止爬虫
        if (pageSize > 100) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        // 分页搜索出userId所收藏的帖子
        Long userId = favourQueryDTO.getUserId();
        QueryWrapper<PostFavour> wrapper = new QueryWrapper<>();
        wrapper.orderBy(StringUtils.isNotBlank(orderBy),sort.equals(OrderConstant.SORT_ORDER_ASC),orderBy);
        wrapper.lambda().eq(PostFavour::getUserId,userId);
        Page<PostFavour> listByPage = favourMapper.selectPage(new Page<>(currPage, pageSize), wrapper);

        if (listByPage.getTotal() == 0) {
            // 如果没有数据那么给前端返回空数组
            List<PostVO> emptyList = new ArrayList<>();
            return PageResultDTO.getSuccessPageResult(0,emptyList);
        }

        List<PostFavour> records = listByPage.getRecords();
        int total = (int) listByPage.getTotal();
        List<Long> postIdList = records.stream().map(PostFavour::getPostId).collect(Collectors.toList());
        // 根据postId查到所有的帖子信息
        List<Post> postList = postMapper.queryPostById(postIdList);
        List<PostVO> postVOList = new ArrayList<>();
        postList.forEach(dto -> {
            PostVO vo = new PostVO();
            BeanUtil.copyProperties(dto,vo);
            postVOList.add(vo);
        });

        return PageResultDTO.getSuccessPageResult(postVOList.size(),postVOList);
    }
}




