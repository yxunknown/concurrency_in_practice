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

import com.google.errorprone.annotations.concurrent.GuardedBy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-04 09:16
 */

public class PrimeGenerator implements Runnable {

    @GuardedBy("this")
    private final List<BigInteger> primes = new ArrayList<>();

    /**
     * covariable to control current thread
     */
    private volatile boolean cancelled = false;


    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            // 如果接下来的代码被阻塞，那么对状态标志地检查可能无法进行
            // 从而导致当前任务无法如客户端期望的那样被取消
            p = p.nextProbablePrime();
            synchronized (this) {
                this.primes.add(p);
            }
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    /**
     * get computed primes
     *
     * @return primes list
     */
    public synchronized List<BigInteger> get() {
        // not publish primes to external
        return new ArrayList<>(primes);
    }

    public static void main(String[] args) {
        PrimeGenerator generator = new PrimeGenerator();
        Thread produce = new Thread(generator);
        Thread consume = new Thread(() -> {
            try {
                while (true) {
                    long size = generator.get().size();
                    System.out.println(size);
                    if (size > 242790) {
                        break;
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                generator.cancel();
            }
        });
        produce.start();
        consume.start();
    }
}
