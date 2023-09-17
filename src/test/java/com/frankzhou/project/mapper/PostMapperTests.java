package com.frankzhou.project.mapper;

import cn.hutool.json.JSONUtil;
import com.frankzhou.project.model.entity.PostFavour;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子数据库sql语句测试
 * @date 2023-04-30
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class PostMapperTests {

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostFavourMapper favourMapper;

    @Test
    public void testInsertForBack() {
        PostFavour postFavour = new PostFavour();
        postFavour.setPostId(12321L);
        postFavour.setUserId(123L);
        int insertCount = favourMapper.insert(postFavour);
        log.info("postFavour:{}", JSONUtil.toJsonStr(postFavour));
    }

    @Test
    public void testBatchInsertForBack() {
        PostFavour postFavour1 = new PostFavour();
        postFavour1.setPostId(12321L);
        postFavour1.setUserId(123L);
        PostFavour postFavour2 = new PostFavour();
        postFavour2.setPostId(121L);
        postFavour2.setUserId(13L);
        List<PostFavour> favourList = new ArrayList<>();
        favourList.add(postFavour1);
        favourList.add(postFavour2);
        Integer insertCount = favourMapper.batchInsert(favourList);
        log.info("list:{}",JSONUtil.toJsonStr(favourList));
    }
}
