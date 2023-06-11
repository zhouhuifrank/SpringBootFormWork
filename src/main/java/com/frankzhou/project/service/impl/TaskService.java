package com.frankzhou.project.service.impl;

import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.config.ThreadPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 使用自定义线程池
 * @date 2023-06-11
 */
@Slf4j
@Service
@EnableAsync
public class TaskService {

    @Autowired
    @Qualifier("commonExecutor")
    private Executor executor;

    @Async
    public void executeTask() {
        executor.execute(() -> {
            log.info("Thread name:{} run",Thread.currentThread().getName());
            throw new BusinessException("子线程执行失败");
        });
        log.info("异步任务执行结束");
    }

    @Async
    public void executeBatchTask() {
        for (int i=0;i<100;i++) {
            executor.execute(() -> {
                log.info("Thread name:{} value:{}",Thread.currentThread().getName());
            });
        }
        log.info("异步任务执行结束");
    }
}
