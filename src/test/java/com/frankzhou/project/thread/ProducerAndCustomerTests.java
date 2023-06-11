package com.frankzhou.project.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 生产者和消费者 等待阻塞和通知唤醒
 * @date 2023-05-28
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerAndCustomerTests {

    @Test
    public void testWaitAndNotify() {
        log.info("Sync wait and notify");

        // Data1 data = new Data1();
        Data2 data = new Data2();
        new Thread(() -> {
            for (int i=0;i<10;i++) {
                try {
                    data.increment();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        },"Thread-A").start();

        new Thread(() -> {
            for (int i=0;i<10;i++) {
                try {
                    data.decrement();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        },"Thread-B").start();

        new Thread(() -> {
            for (int i=0;i<10;i++) {
                try {
                    data.increment();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        },"Thread-C").start();

        new Thread(() -> {
            for (int i=0;i<10;i++) {
                try {
                    data.decrement();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        },"Thread-D").start();
    }

    @Test
    public void testCondition() {

    }

    class Data1 {
        private int number = 0;

        private synchronized void increment() throws InterruptedException {
            while (number != 0) {
                // 等待
                this.wait();
            }
            number++;
            log.info(Thread.currentThread().getName() + "=>" + number);
            this.notifyAll();
        }

        private synchronized void decrement() throws InterruptedException {
            while (number == 0) {
                this.wait();
            }
            number--;
            log.info(Thread.currentThread().getName() + "=>" + number);
            this.notifyAll();
        }
    }

    class Data2 {
        private int number = 0;

        private Lock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();

        private void increment() {
            lock.lock();
            try {
                while (number != 0) {
                    condition.await();
                }
                number++;
                log.info(Thread.currentThread().getName() + "=>" + number);
                condition.signalAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }

        private void decrement() {
            lock.lock();
            try {
                while (number == 0) {
                    condition.await();
                }
                number--;
                log.info(Thread.currentThread().getName() + "=>" + number);
                condition.signalAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}
