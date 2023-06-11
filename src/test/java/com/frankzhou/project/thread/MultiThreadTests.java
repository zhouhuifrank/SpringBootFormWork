package com.frankzhou.project.thread;

import cn.hutool.json.JSONUtil;
import cn.hutool.system.RuntimeInfo;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.mapper.PostFavourMapper;
import com.frankzhou.project.mapper.PostMapper;
import com.frankzhou.project.mapper.PostThumbMapper;
import com.frankzhou.project.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 多线程测试
 * @date 2023-05-27
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MultiThreadTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostFavourMapper postFavourMapper;

    @Resource
    private PostThumbMapper postThumbMapper;

    @Test
    public void testThread() {
        log.info("Main thread start");
        Thread t = new Thread(() -> {
            log.info("Thread start");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Thread end");
        });

        t.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Main thread end");
    }

    @Test
    public void testThread2() {
        new Thread(() -> {
            log.info("Thread start");
        }).start();
        log.info(JSONUtil.toJsonStr(ResultDTO.getSuccessResult()));
    }

    @Test
    public void testThread3() {
        Thread t = new Thread(new MyRunnable());
        t.start();
        log.info("success");
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

    class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Start Thread by Runnable");
        }
    }

    @Test
    public void testThread4() {
        log.info("Start Thread");

        new Thread(() -> {
            log.info(Thread.currentThread().getName() + " run");
            log.info("线程1");
        }).start();

        new Thread(() -> {
            log.info(Thread.currentThread().getName() + " run");
            log.info("线程2");
        }).start();

        new Thread(() -> {
            log.info(Thread.currentThread().getName() + " run");
            log.info("线程3");
        }).start();
    }
}
