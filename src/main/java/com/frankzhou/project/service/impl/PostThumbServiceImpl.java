package com.frankzhou.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankzhou.project.common.ResultCodeConstant;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.mapper.PostMapper;
import com.frankzhou.project.model.dto.postThumb.PostThumbAddDTO;
import com.frankzhou.project.model.dto.user.UserDTO;
import com.frankzhou.project.model.entity.Post;
import com.frankzhou.project.model.entity.PostThumb;
import com.frankzhou.project.redis.RedisKeys;
import com.frankzhou.project.redis.StringRedisUtil;
import com.frankzhou.project.service.PostThumbService;
import com.frankzhou.project.mapper.PostThumbMapper;
import com.frankzhou.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 针对帖子点赞的业务逻辑操作实现
 * @date 2023-04-30
 */
@Slf4j
@Aspect
@Service
public class PostThumbServiceImpl implements PostThumbService {

    @Resource
    private UserService userService;

    @Resource
    private PostThumbMapper postThumbMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisUtil stringRedisUtil;

    @Override
    public ResultDTO<Boolean> doPostThumb(PostThumbAddDTO thumbAddDTO) {
        if (ObjectUtil.isNull(thumbAddDTO) || thumbAddDTO.getPostId() <= 0) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        ResultDTO<UserDTO> loginUserResult = userService.getLoginUser();
        if (loginUserResult.getResultCode() != HttpStatus.HTTP_OK) {
            return ResultDTO.getErrorResult(ResultCodeConstant.USER_NOT_LOGIN);
        }
        UserDTO loginUser = loginUserResult.getData();
        Long userId = loginUser.getId();
        Long postId = thumbAddDTO.getPostId();

        // 锁的粒度为用户
        String lockKey = RedisKeys.POST_THUMB_KEY + userId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean flag = lock.tryLock(10,TimeUnit.SECONDS);
            if (flag) {
                PostThumbService proxy = (PostThumbService) AopContext.currentProxy();
                return proxy.doPostThumbTrans(postId,userId);
            } else {
                log.info("Redisson分布式锁获取失败，请稍后重试");
            }
        } catch (InterruptedException e) {
            log.error("redis分布式锁服务异常");
        } finally {
            // 释放锁
            lock.unlock();
        }

        return ResultDTO.getErrorResult(ResultCodeConstant.DISTRIBUTED_LOCK_FAIL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> doPostThumbTrans(Long postId, Long userId) {
        LambdaQueryWrapper<PostThumb> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostThumb::getPostId,postId)
                .eq(PostThumb::getUserId,userId);
        PostThumb oldPostThumb = postThumbMapper.selectOne(queryWrapper);
        Integer updateCount;
        if (ObjectUtil.isNull(oldPostThumb)) {
            // 如果不存在，那么用户就是点赞
            // 点赞表新增一条记录
            PostThumb postThumb = new PostThumb();
            postThumb.setPostId(postId);
            postThumb.setUserId(userId);
            int insertCount = postThumbMapper.insert(postThumb);
            if (insertCount < 1) {
                throw new BusinessException(ResultCodeConstant.DB_INSERT_COUNT_ERROR);
            }
            // 对应的帖子点赞数加1
            updateCount = postMapper.addPostThumb(postId, 1);
        } else {
            // 如果存在，那么用户就是取消点赞
            // 点赞表删除一条记录
            int deleteCount = postThumbMapper.deleteById(oldPostThumb.getId());
            if (deleteCount < 1) {
                throw new BusinessException(ResultCodeConstant.DB_DELETE_COUNT_ERROR);
            }
            // 对应的帖子点赞减1(乐观锁)
            updateCount = postMapper.addPostFavour(postId, -1);
        }

        if (updateCount < 1) {
            throw new BusinessException(ResultCodeConstant.DB_UPDATE_COUNT_ERROR);
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

}




