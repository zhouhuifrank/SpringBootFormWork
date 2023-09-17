package com.frankzhou.project.model.event.listener;

import com.frankzhou.project.config.ThreadPoolConfig;
import com.frankzhou.project.mapper.UserMapper;
import com.frankzhou.project.model.entity.User;
import com.frankzhou.project.model.event.dto.UserRegisterEvent;
import com.frankzhou.project.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户注册事件监听器
 * @date 2023-08-19
 */
@Slf4j
@Component
public class UserRegisterListener {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtil redisUtil;

    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, fallbackExecution = true)
    public void addScore(UserRegisterEvent registerEvent) {
        // 积分落库
        User user = registerEvent.getUser();
        user.setUserRole("积分到账");
        userMapper.updateById(user);
        // 刷新缓存
        String redisKey = "cache:user:" + user.getUserName();
        redisUtil.deleteObject(redisKey);
        redisUtil.setCacheString(redisKey,user);
        log.info("新增积分事件结束");
    }

    @Async
    @EventListener(classes = UserRegisterEvent.class)
    public void sendMessage(UserRegisterEvent registerEvent) {
        User user = registerEvent.getUser();
        user.setPhone("19858119386");
        userMapper.updateById(user);
        log.info("发送短信事件完成");
    }
}
