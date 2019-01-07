/*
 * Copyright (C) 2019 The concurrency_in_practice Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cha7.section2;

import com.google.errorprone.annotations.concurrent.GuardedBy;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-07 14:50
 */

public class LoggerService {
    private final BlockingQueue<String> queue = new LinkedBlockingDeque<>(1024);
    private final LoggerTask loggerTask;
    private final PrintWriter writer;

    @GuardedBy("this")
    private boolean isShutdown = false;
    @GuardedBy("this")
    private int reservations = 0;

    public LoggerService(PrintWriter writer) {
        this.writer = writer;
        this.loggerTask = new LoggerTask();
    }

    public void start() {
        this.loggerTask.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        // 调用LoggerTask的interrupt方法
        // 因为BlockingQueue的take方法会响应InterruptedException
        // 因此能够保证LoggerTask可以接收到中断请求
        loggerTask.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            // 当外部调用stop方法后
            // isShutdown标志会被设置为true
            // 这里检查到该标志值后，会抛出异常从而拒绝新的日志内容进入消息队列
            if (isShutdown) {
                throw new IllegalStateException("Logger is shut down");
            }
            // 通过检查，获得添加日志进入队列的权利
            ++reservations;
        }
        this.queue.put(msg);
    }

    public static void main(String[] args) throws InterruptedException {
        LoggerService service = new LoggerService(new PrintWriter(System.out, true));
        for (int i = 0; i < 50; i++) {
            service.log(Thread.currentThread().getName() + " " + Instant.now() + " " + i);
        }
        service.start();
        Thread.sleep(200);
        service.stop();
        for (int i = 50; i < 60; i++) {
            service.log(Thread.currentThread().getName() + " " + Instant.now() + " " + i);
        }
    }

    private class LoggerTask extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (LoggerService.this) {
                            // 日志服务已经被关闭且队列中没有剩余的日志内容
                            // 跳出while循环，从而使得JVM可以结束
                            if (isShutdown && reservations == 0) {
                                break;
                            }
                        }
                        String msg = queue.take();
                        synchronized (LoggerService.this) {
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException e) {
                        // retry
                        // 响应中断请求，等到队列中所有日志内容处理完毕
                        // 结束该线程
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
}
