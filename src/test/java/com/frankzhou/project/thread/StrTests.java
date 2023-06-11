package com.frankzhou.project.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-05-30
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class StrTests {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testString() {
        String sql = "select customer_no from cus group by customer_no having count(*) > 2";
        int idx = sql.indexOf("group");
        String checkSql = sql.substring(0,idx) + "where row_date = '20232452' " + sql.substring(idx);
        System.out.println(checkSql);
    }

    @Test
    public void testRedisList() {
        String key = "cache:db:";
        List<String> tableList = new ArrayList<>();
        tableList.add("a");
        tableList.add("c");
        tableList.add("d");
        redisTemplate.opsForList().leftPushAll(key,tableList);
    }

    @Test
    public void testGetList() {
        String key = "cache:db:";
        List<Object> ret = redisTemplate.opsForList().range(key, 0, -1);
        for (Object table : ret) {
            System.out.println(table);
        }
        System.out.println(ret.get(0));
    }

    @Test
    public void testStrRedis() {
        String key = "cache:db:";
        List<String> tableList = new ArrayList<>();
        tableList.add("A");
        tableList.add("B");
        tableList.add("C");
        stringRedisTemplate.opsForList().rightPushAll(key, tableList);
        List<String> ret = stringRedisTemplate.opsForList().range(key, 0, -1);
        System.out.println(ret);
        for (String t : ret) {
            System.out.println(t);
        }
    }

    @Test
    public void testTrim() {
        String str = "Hello world   ";
        System.out.println(str);
        str = str.trim();
        System.out.println(str);
    }

    @Test
    public void testFilePath() {
        String filePath = "/home/datafairy/file/hadoop/local" + File.separator;
        System.out.println(filePath);
        System.out.println(Paths.get(filePath).getFileName());
        // 获取系统当前目录
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            filePath = System.getProperty("user.dir") + File.separator + "hadoopFile" + File.separator;
        }
        System.out.println(filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("文件不存在");
        }

        System.out.println(file.getAbsoluteFile());
    }
}
