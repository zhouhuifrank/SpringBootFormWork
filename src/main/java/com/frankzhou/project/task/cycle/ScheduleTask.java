package com.frankzhou.project.task.cycle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 定时任务线程池
 * @date 2023-08-12
 */
public class ScheduleTask {

    public static void main(String[] args) {
        ScheduledExecutorService es = Executors.newScheduledThreadPool(5);

        es.scheduleAtFixedRate(() -> {
            System.out.println("hello-world1");
        },0,5, TimeUnit.SECONDS);

        es.scheduleWithFixedDelay(() -> {
            System.out.println("hello-world2");
        },0,3,TimeUnit.SECONDS);
    }
}
