package com.frankzhou.project.thread;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.config.ThreadPoolConfig;
import com.frankzhou.project.mapper.UserMapper;
import com.frankzhou.project.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-08-19
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class BatchCompletableFutureTests {

    @Resource
    private UserMapper userMapper;

    @Resource(name = ThreadPoolConfig.COMMON_EXECUTOR)
    private Executor commonExecutor;

    @Test
    public void testBatchTask() {
        log.info("任务开始");
        List<String> dataSourceList = new ArrayList<>();
        for (int i=1;i<=102;i++) {
            dataSourceList.add("dataSource-" + i);
        }

        Integer batchSize = 10;
        Integer futureSize = dataSourceList.size() / batchSize;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i=0;i<futureSize;i++) {
            List<String> subList = dataSourceList.subList(i * batchSize, (i + 1) * batchSize);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int j=0;j<subList.size();j++) {
                    log.info(subList.get(j));
                }}, commonExecutor);
            futureList.add(future);
        }
        // 补偿剩余的list
        List<String> subList = dataSourceList.subList(futureSize*batchSize,dataSourceList.size());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            for (int j=0;j<subList.size();j++) {
                log.info(subList.get(j));
            }
        }, commonExecutor);
        futureList.add(future);

        log.info("future对象的个数为{}",futureList.size());
        // 聚合future对象
        // List<Void> futureCombine = futureList.stream().map(CompletableFuture::join).collect(Collectors.toList());
        CompletableFuture<Void>[] futureArray = futureList.stream().toArray(CompletableFuture[]::new);
        CompletableFuture<Void> futureCombine = CompletableFuture.allOf(futureArray);
        try {
            // 阻塞等待
            futureCombine.get();
            log.info("异步任务执行成功");
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

        // 所有future完成后，进行接下来的任务
        log.info("开始父子节点信息初始化");
    }

    @Test
    public void testSerialTask() {
        log.info("任务开始");
        List<String> dataSourceList = new ArrayList<>();
        for (int i=0;i<102;i++) {
            dataSourceList.add("dataSource-" + i);
        }

        for (int i=0;i<dataSourceList.size();i++) {
            log.info(dataSourceList.get(i));
        }
        log.info("任务执行结束");
    }

    @Test
    public void testBatchInsertAsync() {
        log.info("开始执行任务");
        List<User> userList = new ArrayList<>();
        for (int i=1;i<=1002;i++) {
            User user = User.builder()
                    .userName("user"+i)
                    .userAccount("user0000"+i)
                    .build();
            userList.add(user);
        }

        Integer batchSize = 100;
        Integer futureSize = userList.size() / batchSize;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i=0;i<futureSize;i++) {
            List<User> subList = userList.subList(i * batchSize, (i + 1) * batchSize);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int j=0;j<subList.size();j++) {
                    userMapper.insert(subList.get(j));
                }
            }, commonExecutor);
            log.info("异步任务{}执行结束",(i+1));
            futureList.add(future);
        }
        // 补偿剩余的列表元素
        List<User> subList = userList.subList(futureSize * batchSize, userList.size());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            for (int j=0;j<subList.size();j++) {
                userMapper.insert(subList.get(j));
            }
        }, commonExecutor);
        log.info("补偿异步任务执行结束");
        futureList.add(future);

        log.info("并行任务数{}",futureList.size());

        CompletableFuture[] futureArray = futureList.stream().toArray(CompletableFuture[]::new);
        CompletableFuture<Void> combineFuture = CompletableFuture.allOf(futureArray);
        // 阻塞等待
        try {
            combineFuture.get();
            log.info("阻塞等待");
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

        // 继续下面的任务
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.likeRight(User::getUserName,"user")
                .orderByDesc(User::getUserName);
        List<User> result = userMapper.selectList(userWrapper);
        for (User user : result) {
            log.info("查询结果，用户:{}",user.getUserName());
        }
        log.info("任务结束");
    }

    @Test
    public void testThenRun() {
        log.info("开始执行任务");
        List<User> userList = new ArrayList<>();
        for (int i=1;i<=1002;i++) {
            User user = User.builder()
                    .userName("user"+i)
                    .userAccount("user0000"+i)
                    .build();
            userList.add(user);
        }

        Integer batchSize = 100;
        Integer futureSize = userList.size() / batchSize;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i=0;i<futureSize;i++) {
            List<User> subList = userList.subList(i * batchSize, (i + 1) * batchSize);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int j=0;j<subList.size();j++) {
                    userMapper.insert(subList.get(j));
                }
            }, commonExecutor);
            log.info("异步任务{}执行结束",(i+1));
            futureList.add(future);
        }
        // 补偿剩余的列表元素
        List<User> subList = userList.subList(futureSize * batchSize, userList.size());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            for (int j=0;j<subList.size();j++) {
                userMapper.insert(subList.get(j));
            }
        }, commonExecutor).exceptionally(ex -> {
            log.warn(ex.getMessage());
            return null;
        });
        log.info("补偿异步任务执行结束");
        futureList.add(future);

        log.info("并行任务数{}",futureList.size());

        CompletableFuture[] futureArray = futureList.stream().toArray(CompletableFuture[]::new);
        CompletableFuture<Void> combineFuture = CompletableFuture.allOf(futureArray);

        CompletableFuture<Void> runAsync = combineFuture.thenRunAsync(() -> {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.likeRight(User::getUserName, "user")
                    .orderByDesc(User::getUserName);
            List<User> result = userMapper.selectList(userWrapper);
            for (User user : result) {
                log.info("查询结果，用户:{}", user.getUserName());
            }
        },commonExecutor);

        log.info("任务执行结束");
        try {
            runAsync.get();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Test
    public void testBatchInsertSync() {
        log.info("开始执行任务");
        List<User> userList = new ArrayList<>();
        for (int i=1;i<=1002;i++) {
            User user = User.builder()
                    .userName("user"+i)
                    .userAccount("user0000"+i)
                    .build();
            userList.add(user);
        }

        for (int i=0;i<userList.size();i++) {
            userMapper.insert(userList.get(i));
        }

        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.likeRight(User::getUserName,"user")
                .orderByDesc(User::getUserName);
        List<User> result = userMapper.selectList(userWrapper);
        for (User user : result) {
            log.info("查询结果，用户:{}",user.getUserName());
        }
        log.info("任务结束");
    }
}
