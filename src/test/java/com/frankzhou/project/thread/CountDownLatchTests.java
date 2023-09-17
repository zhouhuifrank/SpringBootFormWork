package com.frankzhou.project.thread;

import com.frankzhou.project.config.ThreadPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.recycler.Recycler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 并发计数器
 * @date 2023-06-15
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class CountDownLatchTests {

    @Resource(name = ThreadPoolConfig.COMMON_EXECUTOR)
    private Executor executor;

    @Test
    public void testCountDownLatch() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);

        IntStream.range(0,10).forEach(i -> {
            executor.execute(() -> {
                try {
                    Thread.sleep(2000);
                    log.info("Thread name:{} run",Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        countDownLatch.await();
        log.info("线程执行结束");
    }

    @Test
    public void testBatchCountDownLatch() throws InterruptedException {
        int size = 100;
        int batchSize = 2;
        int batchCount = size / batchSize;

        for (int i=0;i<batchCount;i++) {
            // 同一时间可以执行batchSize个规则线程
            CountDownLatch latch = new CountDownLatch(batchSize);
            log.info("任务执行批次:{}",i);;
            IntStream.range(0,batchSize).forEach(j -> {
                executor.execute(() -> {
                    log.info("Thread name:{} value:{}",Thread.currentThread().getName(),j);
                    latch.countDown();
                });
            });
            latch.await();
            log.info("任务执行批次:{}完成",i);
        }
    }
}
