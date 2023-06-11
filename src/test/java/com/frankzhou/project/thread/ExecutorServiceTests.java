package com.frankzhou.project.thread;

import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.service.impl.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 线程池测试
 * @date 2023-05-27
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExecutorServiceTests {

    @Resource
    private ThreadPoolTaskExecutor executor;

    @Resource
    private TaskService taskService;

    @Test
    public void testSingleThreadPool() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        try {
            for (int i=0;i<100;i++) {
                es.submit(() -> {
                    log.info(Thread.currentThread().getName() + "run");
                });
            }
        } catch (Exception e) {
            log.info("wrong");
        } finally {
            es.shutdown();
        }

        log.info("success");
    }

    @Test
    public void testFixedThreadPool() {
        ExecutorService es = Executors.newFixedThreadPool(10);
        try {
            for (int i=0;i<100;i++) {
                es.submit(() -> {
                    log.info(Thread.currentThread().getName() + "run");
                });
            }
        } catch (Exception e) {
            log.info("wrong");
        } finally {
            es.shutdown();
        }

        log.info("success");
    }

    @Test
    public void testCachedThreadPool() {
        ExecutorService es = Executors.newCachedThreadPool();
        try {
            for (int i=0;i<100;i++) {
                es.submit(() -> {
                    log.info(Thread.currentThread().getName() + "run");
                });
            }
        } catch (Exception e) {
            log.info("wrong");
        } finally {
            es.shutdown();
        }

        log.info("success");
    }

    @Test
    public void testScheduleThreadPool() {
        // 定时任务线程
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(5);
        try {
            ses.scheduleWithFixedDelay(() -> {
                log.info("定时任务调度线程");
            },2,1,TimeUnit.MINUTES);
        } catch (Exception e) {
            log.info("wrong");
        } finally {
            ses.shutdown();
        }

        log.info("success");
    }

    @Test
    public void testThreadPool() {
        // 手动创建线程池
        ExecutorService es = new ThreadPoolExecutor(2,5,3, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(3),Executors.defaultThreadFactory(),new ThreadPoolExecutor.DiscardOldestPolicy());
        try {
            for (int i=0;i<100;i++) {
                es.submit(() -> {
                    log.info(Thread.currentThread().getName() + "run");
                    throw new BusinessException("原始线程池执行失败");
                });
            }
        } catch (Exception e) {
            log.info("wrong");
        } finally {
            es.shutdown();
        }

        log.info("success");
    }

    @Test
    public void testMyThreadExecutor() {
        // 能够在主线程中打印出抛出的异常信息
        taskService.executeTask();
    }

    @Test
    public void testBatchExecutor() {
        taskService.executeBatchTask();
    }

}
