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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-04 13:02
 */

public class TimeRunner {


    private static final ScheduledExecutorService cancelExecutor = new ScheduledThreadPoolExecutor(10,
        new ThreadFactoryBuilder().setNameFormat("delay-task-pool-%d").build(),
        new ThreadPoolExecutor.AbortPolicy());

    public static void timeRun(final Runnable task,
                               long timeout,
                               TimeUnit unit) throws Exception {
        RethrowableTask rethrowableTask = new RethrowableTask(task);
        final Thread taskThread = new Thread(rethrowableTask);
        // execute task
        taskThread.start();
        // delay some time to execute this cancel task
        cancelExecutor.schedule(() -> {
            // interrupt task thread
            System.out.println(Thread.currentThread().getName() + " try to cancel task");
            taskThread.interrupt();
        }, timeout, unit);
        // wait some time for this thread to die
        // 这里依赖了join的实现
        // 导致了一个问题：即无法之后是因为join方法超时而返回还是因为线程正常结束而返回
        taskThread.join(unit.toMillis(timeout));
        /*
          ExecutorService的shutdown方法，用于平滑关闭线程池，调用该方法后，程序后在所有已经提交的任务
          执行完毕后退出。此时不再接受新的任务！可以用awaitTerminated方法来等待当前线程池关闭。
          而shutdownNow则尝试立即关闭所有任务，以提交的任务不会得到完成，新的任务也无法进入。
         */
        cancelExecutor.shutdown();
        rethrowableTask.rethrow();
    }

    private static class RethrowableTask implements Runnable {

        private volatile Throwable t;
        private final Runnable task;

        RethrowableTask(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                task.run();
            } catch (Throwable t) {
                this.t = t;
            }
        }

        void rethrow() throws Exception{
            if (t != null) {
                throw new Exception(t);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Runnable task = () -> {
            try {
                int i = 0;
                while (true) {
                    System.out.println(i++);
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        timeRun(task, 1000, TimeUnit.MILLISECONDS);
    }
}
