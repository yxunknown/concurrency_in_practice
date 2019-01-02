package cha6.section2;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolTask {
    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors() * 2 + 1,
        new ThreadFactoryBuilder().setNameFormat("scheduled-pool-%d").setDaemon(true).build(),
        new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws InterruptedException {
        executorService.schedule(new ThrowableTask(), 1, TimeUnit.SECONDS);
        Thread.sleep(1000);
        // ScheduledThreadPoolExecutor建立多个线程来执行任务，当某个任务发生异常时，也不会发送线程泄露的现象
        // ScheduledThreadPoolExecutor内部使用DelayQueue来实现调度服务。
        executorService.schedule(new ThrowableTask(), 1, TimeUnit.SECONDS);
        Thread.sleep(5000);
    }

    static class ThrowableTask implements Runnable {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " running");
            throw new RuntimeException("Just break the runtime");
        }
    }
}
