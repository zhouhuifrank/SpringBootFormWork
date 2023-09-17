package com.frankzhou.project.task.cycle;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description xxl-job基本操作
 * @date 2023-08-13
 */
@Slf4j
@Component
public class XxlJobDemoTask {

    // 云服务器部署的xxl-job需要内网穿透才能执行
    @XxlJob(value = "frankTestHandler")
    public void testXxlJob() {
        log.info("任务测试开始");

        for (int i=1;i<=5;i++) {
            log.info("任务测试中,当前结果为:{}",i);
            System.out.println("任务执行成功");
        }

        log.info("任务测试结束");
    }
}
