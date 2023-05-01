package com.frankzhou.project.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子数据库sql语句测试
 * @date 2023-04-30
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PostMapperTests {

    @Resource
    private PostMapper postMapper;

    @Test
    public void testAddThumbNum() {

    }

    @Test
    public void testAddFavourNum() {

    }
}
