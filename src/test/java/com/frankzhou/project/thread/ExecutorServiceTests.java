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

    @Resource()
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
    public void testExecutor() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(() -> {
            log.info("Thread {} is running",Thread.currentThread().getName());
            int a = 10/0;
        },"test-executor");
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

    @Test
    public void timeOutFuse() {
        // 线程超时熔断案例
        ExecutorService executor = Executors.newFixedThreadPool(5);
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                // 执行任务逻辑，需要处理异常
                Thread.sleep(5000);
                return "time out fusing";
            }
        });
        try {
            String result = future.get(3, TimeUnit.SECONDS);
            log.info("任务执行结束，任务结果:{}",result);
        } catch (TimeoutException e) {
            // 超时熔断
            future.cancel(true);
            log.info("任务执行超时，进行熔断处理");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBatchExecuteTask() throws InterruptedException {
        // 分批处理任务案例
        Integer taskCount = 100;
        Integer batchSize = 10;

        ExecutorService executor = Executors.newFixedThreadPool(batchSize);
        CountDownLatch latch = new CountDownLatch(batchSize);

        for (int i=1;i<=taskCount;i++) {
            CountDownLatch finalLatch = latch;
            executor.submit(() -> {
                try {
                    // 任务具体逻辑
                    log.info("do something");
                } finally {
                    finalLatch.countDown();
                }
            });

            // 执行玩一个批次
            if (i % batchSize == 0) {
                // 当达到任务执行的批次,必须阻塞等待该批次任务全部执行结束
                latch.await();
                //  执行完一个批次，需要重新创建
                latch = new CountDownLatch(batchSize);
            }
        }

        executor.shutdown();
    }

    @Test
    public void testParallelQueryTask() {
        // 并行化查询案例
        CompletableFuture<String> query1 = CompletableFuture.supplyAsync(() -> {
            return "query result 1";
        });

        CompletableFuture<String> query2 = CompletableFuture.supplyAsync(() -> {
            return "query result 2";
        });

        // 并行处理两个查询任务的结果
        CompletableFuture<Void> combineFuture = CompletableFuture.allOf(query1, query2);

        combineFuture.thenRun(() -> {
            try {
                // 获取两个查询的结果
                String result1 = query1.get();
                String result2 = query2.get();

                log.info("result1: {}",result1);
                log.info("result2: {}",result2);
            } catch (Exception e) {
                // 异常处理
                e.printStackTrace();
            }
        });

        try {
            combineFuture.get();
        } catch (Exception e) {
            // 异常处理
            e.printStackTrace();
        }
    }
}
