package com.frankzhou.project.task.cycle;


import java.util.Timer;
import java.util.TimerTask;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 定时器
 * @date 2023-08-12
 */
public class TimeTask {

    public static void main(String[] args) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello-world");
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask,10,3000);
    }
}
