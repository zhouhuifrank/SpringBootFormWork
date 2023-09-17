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

    @Resource(name = ThreadPoolConfig.COMMON_EXECUTOR)
    private Executor executor;

    @Async(ThreadPoolConfig.COMMON_EXECUTOR)
    public void executeTask() {
        log.info("Thread name:{}",Thread.currentThread().getName());
        int a = 10/0;
    }

    @Async(ThreadPoolConfig.RULER_EXECUTOR)
    public void executeBatchTask() {
        for (int i=0;i<100;i++) {
            log.info("Thread name:{}",Thread.currentThread().getName());
        }
        log.info("异步任务执行结束");
    }
}
