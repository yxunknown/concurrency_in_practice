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

import java.util.*;
import java.util.concurrent.*;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-07 16:43
 */

public class TrackingExecutorService extends AbstractExecutorService {

    private ExecutorService executorService = new ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2,
        1000L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(1024),
        new ThreadFactoryBuilder().setNameFormat("Tracking-thread-pool-%d").build(),
        new ThreadPoolExecutor.AbortPolicy());

    private Set<Runnable> cancelledTasks = Collections.synchronizedSet(new HashSet<>(32));

    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(() -> {
            try {
                command.run();
            } finally {
                // 如果当前任务在执行的过程中被shutdownNow方法中断
                // 则将其放入已关闭任务队列中
                if (isShutdown() && Thread.currentThread().isInterrupted()) {
                    cancelledTasks.add(command);
                }
            }
        });
    }

    public List<Runnable> getCancelledTasks() {
        if (!isTerminated()) {
            throw new IllegalStateException("Executor is running");
        }
        return new ArrayList<>(cancelledTasks);
    }
}
