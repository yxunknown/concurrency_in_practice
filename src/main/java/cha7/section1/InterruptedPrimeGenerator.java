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

import org.omg.PortableServer.THREAD_POLICY_ID;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-04 10:24
 */

public class InterruptedPrimeGenerator extends Thread {

    private final BlockingQueue<BigInteger> queue;

    public InterruptedPrimeGenerator(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
        setName("prime-generator");
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted()) {
                p = p.nextProbablePrime();
                // put方法虽然会阻塞
                // 但是put方法会响应外部中断，从而提前退出
                queue.put(p);
                System.out.println(Thread.currentThread().getName() + " generates a prime: " + p);
            }
            System.out.println(Thread.currentThread().getName() + " is stopped.");
        } catch (InterruptedException e) {
            System.out.println("Prime generator done!");
        }
    }

    public void cancel() {
        interrupt();
    }

    public static void main(String[] args) {
        BlockingQueue<BigInteger> primeQueue = new LinkedBlockingDeque<>(100);
        InterruptedPrimeGenerator generator = new InterruptedPrimeGenerator(primeQueue);
        Thread consume = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    System.out.println(primeQueue.take());
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                System.out.println(Thread.currentThread().getName() + " try to cancel prime generator.");
                generator.cancel();
            }
        });
        generator.start();
        consume.start();
    }
}
