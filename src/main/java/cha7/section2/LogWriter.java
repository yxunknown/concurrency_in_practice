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

import java.io.PrintWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-04 16:58
 */

public class LogWriter {
    private final BlockingQueue<String> queue = new LinkedBlockingDeque<>(1024);

    private final LoggerThread logger;

    public volatile boolean shutdownRequest = false;

    public LogWriter(PrintWriter writer) {
        this.logger = new LoggerThread(writer, this.queue);
        start();
    }

    private void start() {
        logger.start();
    }

    public void log(String msg) {
        try {
            if (!shutdownRequest) {
                this.queue.put("LOGGER INFO " + msg);
            } else {
                // 不可靠的关闭服务
                throw new IllegalStateException("Logger is shut down");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        PrintWriter w = new PrintWriter(System.out, true);
        LogWriter logWriter = new LogWriter(w);
        for (int i = 0; i < 100; i++) {
            logWriter.log(Thread.currentThread().getName() + " " + Instant.now() + " " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }
}

class LoggerThread extends Thread {

    private final PrintWriter writer;
    private final BlockingQueue<String> queue;

    LoggerThread(PrintWriter writer, BlockingQueue<String> queue) {
        this.writer = writer;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 日志线程模式存在的一个问题：
                // 当处理日志的速度远远慢于接收日志的速度时
                // 那些时候日志服务的客户端会被阻塞
                writer.println(this.queue.take());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            writer.close();
        }
    }
}
