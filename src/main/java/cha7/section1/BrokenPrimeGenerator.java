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

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-04 09:44
 */

public class BrokenPrimeGenerator extends Thread {
    private final BlockingQueue<BigInteger> queue;
    private volatile boolean cancelled = false;

    public BrokenPrimeGenerator(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!cancelled) {
                p = p.nextProbablePrime();
                // put method is blocked
                queue.put(p);
                System.out.println("prime generator generate a new prime: " + p);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    public static void main(String[] args) {
        BlockingQueue<BigInteger> primeQueue = new LinkedBlockingDeque<>(100);
        BrokenPrimeGenerator generator = new BrokenPrimeGenerator(primeQueue);
        Thread consume = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++){
                    System.out.println(primeQueue.take());
                    sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + " try to cancel prime generator.");
                generator.cancel();
            }
        });
        generator.start();
        consume.start();
    }
}
