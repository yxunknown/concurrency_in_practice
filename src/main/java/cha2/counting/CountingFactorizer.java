package cha2.counting;

import anotation.NotThreadSafe;
import anotation.ThreadSafe;

import java.util.concurrent.atomic.AtomicLong;

public class CountingFactorizer {

    private int count = 0;

    /**
     * thread not safe
     */
    @NotThreadSafe
    public int count() {
        // count++ 的实质
        // int temp = count;    read
        // temp = temp + 1;     modify
        // count = temp;        write
        // 这类操作被成为复合操作
        return ++count;
    }

    // 将复合操作变为原子操作，从而实现线程安全
    private AtomicLong atomicLong = new AtomicLong(0);
    @ThreadSafe
    public long safeCount() {
        return atomicLong.incrementAndGet();
    }
}
