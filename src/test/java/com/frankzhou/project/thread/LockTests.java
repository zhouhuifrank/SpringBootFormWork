package com.frankzhou.project.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Synchronized和Lock
 * @date 2023-05-28
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class LockTests {

    @Test
    public void synchronizedTest() {
        log.info("Thread Start");
        TrainTicketSync ticket = new TrainTicketSync();
        Integer saleNum = 40;
        new Thread(() -> {
            for (int i=0;i<=saleNum;i++) {
                ticket.saleTicket();
            }
        },"Thead-A").start();

        new Thread(() -> {
            for (int i=0;i<=saleNum;i++) {
                ticket.saleTicket();
            }
        },"Thread-B").start();

        new Thread(() -> {
            for (int i=0;i<=saleNum;i++) {
                ticket.saleTicket();
            }
        },"Thread-C").start();
        log.info("Thread End");
    }

    @Test
    public void lockTest() {
        log.info("Thread Start");
        TrainTicketLock ticket = new TrainTicketLock();
        Integer saleNum = 40;
        new Thread(() -> {
            for (int i=0;i<=saleNum;i++) {
                ticket.saleTicket();
            }
        },"Thead-A").start();

        new Thread(() -> {
            for (int i=0;i<=saleNum;i++) {
                ticket.saleTicket();
            }
        },"Thread-B").start();

        new Thread(() -> {
            for (int i=0;i<=saleNum;i++) {
                ticket.saleTicket();
            }
        },"Thread-C").start();
        log.info("Thread End");
    }

    class TrainTicketSync {
        private Integer ticketNum = 50;

        private void saleTicket() {
            synchronized (TrainTicketSync.class) {
                if (ticketNum > 0) {
                    ticketNum--;
                    log.info(Thread.currentThread().getName() + ":当前剩余票数{}",ticketNum);
                }
            }
        }
    }

    class TrainTicketLock {
        private Integer ticketNum = 50;

        private Lock lock = new ReentrantLock();

        private void saleTicket() {
            lock.lock();
            try {
                if (ticketNum > 0) {
                    ticketNum--;
                    log.info(Thread.currentThread().getName() + ":当前剩余票数{}",ticketNum);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
