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

package cha7.section3;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-09 15:52
 */

public class HandleException {
    private ExecutorService service = new ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2,
        1000L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(1024),
        new ThreadFactoryBuilder()
            .setNameFormat("executor-pool-%d")
            .setUncaughtExceptionHandler((t, e) -> {
                // 添加未捕获异常处理器
                Logger logger = Logger.getAnonymousLogger();
                logger.log(Level.SEVERE, "Thread terminated with exception:" + t.getName());
            })
            .build(),
        new ThreadPoolExecutor.AbortPolicy());

    public void submit(Runnable task) {
        this.service.execute(task);
    }

    public static void main(String[] args) {
        HandleException e = new HandleException();
        Runnable task = () -> {
            System.out.println("hhh");
            throw new RuntimeException("i am dying");
        };
        e.submit(task);
        e.submit(() -> {
            System.out.println("i am ok");
        });
    }
}
