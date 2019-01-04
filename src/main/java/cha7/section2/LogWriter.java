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
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-04 16:58
 */

public class LogWriter {
    private final BlockingQueue<String> queue = new LinkedBlockingDeque<>(1024);

    private final LoggerThread logger;

    public LogWriter(Writer writer) {
        this.logger = new LoggerThread(writer);
        start();
    }

    private void start() {
        logger.start();
    }

    public void log(String msg) {
        try {
            this.queue.put(msg);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class LoggerThread extends Thread {

        private final PrintWriter writer;

        LoggerThread(Writer writer) {
            this.writer = new PrintWriter(writer);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    writer.println(LogWriter.this.queue.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writer.close();
            }
        }
    }

    public static void main(String[] args) {
        PrintWriter w = new PrintWriter(System.out, true);
        w.println("xxxx");
        LogWriter logWriter = new LogWriter(new PrintWriter(System.out, true));
        for (int i = 0; i < 100; i++) {
            logWriter.log(Thread.currentThread().getName() + " " + Instant.now() + " " + i);
        }

    }
}
