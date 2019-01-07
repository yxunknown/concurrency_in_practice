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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.PrintWriter;
import java.util.concurrent.*;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-07 15:36
 */

public class LoggerBasedOnExecutor {
    private final ExecutorService exec = new ThreadPoolExecutor(1,
        1,
        0L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(1024),
        new ThreadFactoryBuilder().setNameFormat("Logger-pool-%d").build(),
        new ThreadPoolExecutor.AbortPolicy());

    private final PrintWriter writer;

    public LoggerBasedOnExecutor(PrintWriter writer) {
        this.writer = writer;
    }

    public void stop() {
        try {
            exec.shutdown();
            // wait until all task has been completed
            exec.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // ignore
        } finally {
            writer.close();
        }
    }

    public void log(String msg) {
        try {
            exec.execute(new WriteTask(writer, msg));
        } catch (RejectedExecutionException e) {
            throw new IllegalStateException("Logger is shutdown");
        }
    }

    public static void main(String[] args) {
        LoggerBasedOnExecutor logger = new LoggerBasedOnExecutor(new PrintWriter(System.out));
        for (int i = 0; i < 50; i++) {
            logger.log("i: " + i);
        }
        logger.stop();
        logger.log("after stop");
    }
}

class WriteTask implements Runnable {

    private final PrintWriter writer;
    private final String msg;
    public WriteTask(PrintWriter writer, String msg) {
        this.writer = writer;
        this.msg = msg;
    }

    @Override
    public void run() {
        writer.println(msg);
        writer.flush();
    }
}
