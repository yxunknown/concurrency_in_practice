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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-07 15:56
 */

public class CountService {
    public static final int POISON_PILL = -120231;

    private final CountTask countTask;

    private BlockingQueue<Integer> queue = new LinkedBlockingDeque<>(1024);

    public CountService() {
        this.countTask = new CountTask(queue);
    }

    public void start() {
        this.countTask.start();
    }

    public void stop() throws InterruptedException {
        this.queue.put(POISON_PILL);
    }

    public void awaitTermination() throws InterruptedException {
        this.countTask.join();
    }

    public void count(int i) {
        try {
            this.queue.put(i);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        CountService countService = new CountService();
        for (int i = 0; i < 50; i++) {
            countService.count(i);
        }
        countService.start();
        try {
            countService.stop();
//            countService.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

}

class CountTask extends Thread {

    private final BlockingQueue<Integer> queue;

    public CountTask(BlockingQueue<Integer> queue) {
        this.queue = queue;
        setName("CountTaskThread");
    }

    @Override
    public void run() {
        while (true) {
            try {
                int num = queue.take();
                if (num == CountService.POISON_PILL) {
                    break;
                }
                System.out.println(getName() + " count " + num);
            } catch (InterruptedException e) {
                // ignore
                // retry
            }
        }
    }
}
