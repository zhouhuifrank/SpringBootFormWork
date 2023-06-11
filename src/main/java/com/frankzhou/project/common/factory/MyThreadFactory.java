package com.frankzhou.project.common.factory;

import com.frankzhou.project.common.handler.GlobalUncaughtExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 自定义线程工厂类
 * @date 2023-06-11
 */
@Slf4j
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    private final ThreadFactory factory;

    @Override
    public Thread newThread(Runnable r) {
        // 使用ThreadFactory创建线程，重新设置异常处理器就可以
        Thread thread = factory.newThread(r);
        thread.setUncaughtExceptionHandler(new GlobalUncaughtExceptionHandler());
        return thread;
    }
}
