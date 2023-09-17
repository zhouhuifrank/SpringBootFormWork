package com.frankzhou.project.es;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description es测试类
 * @date 2023-08-20
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DocumentTests {

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testCreateIndex() {
        System.out.println("索引创建完成");
    }
}
