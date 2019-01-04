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

package cha7.section1;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-04 14:10
 */

public class FutureTimerRunner {

    /**
     * task executor
     */
    private static final ExecutorService TASK_EXEC = new ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2,
        1000L,
        TimeUnit.MILLISECONDS,
        // contains at most 1024 tasks
        new LinkedBlockingDeque<>(1024),
        new ThreadFactoryBuilder().setNameFormat("timer-task-pool-%d").build(),
        new ThreadPoolExecutor.AbortPolicy());

    public static void timerRunner(Runnable task,
                                   long timeout,
                                   TimeUnit unit) throws InterruptedException {
        Future<?> future = TASK_EXEC.submit(task);
        try {
            future.get(timeout, unit);
        } catch (TimeoutException e) {
            System.out.println(e.getMessage());
            // task execution expires
        } catch (ExecutionException e) {
            throw new InterruptedException(e.getMessage());
        } finally {
            // if current task is running at one thread
            // then interrupt that thread
            future.cancel(true);
        }
    }

    public static void main(String[] args) {
        Runnable countSheep = () -> {
            long sheep = 0;
            try {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + " counting sheep: " + sheep++);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // sleep interrupted
                // restore status into interrupted
                Thread.currentThread().interrupt();
            }
        };
        try {
            timerRunner(countSheep, 5000, TimeUnit.MILLISECONDS);
            // waiting for all task execute compete
            // shutdown the executor service
            TASK_EXEC.awaitTermination(0, TimeUnit.MILLISECONDS);
            TASK_EXEC.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

}
