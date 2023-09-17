package com.frankzhou.project.task.cycle;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Spring Task定时任务
 * @date 2023-08-12
 */
@Component
@EnableScheduling
public class SpringTask {

    // @Scheduled(cron = "30 * * * * ?")
    public void cronTask() {
        System.out.println("hello-world1");
    }

    // @Scheduled(initialDelay = 0, fixedRate = 3, timeUnit = TimeUnit.SECONDS)
    public void fixedRateTask() {
        System.out.println("hello-world2");
    }

    // @Scheduled(initialDelay = 0, fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void fixedDelayTask() {
        System.out.println("hello-world3");
    }
}
