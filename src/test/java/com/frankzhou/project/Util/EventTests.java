package com.frankzhou.project.Util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.project.mapper.UserMapper;
import com.frankzhou.project.model.entity.User;
import com.frankzhou.project.model.event.dto.UserRegisterEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Spring 事件发布机制测试
 * @date 2023-08-19
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class EventTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Test
    public void testEventPublish() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getUserName,"user900");
        User targetUser = userMapper.selectOne(wrapper);
        eventPublisher.publishEvent(new UserRegisterEvent(this, targetUser));
        log.info("事件发布完成");
    }
}
