package cha2.lock;

import anotation.NotThreadSafe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UnsafeAtomic {
    /**
     * 对于多组原子操作而言，如果不能保证多组原子操作同时被执行，那么仍然无法保证多线程的安全性。
     * 也就是多组原子操作的执行也应该是原子的。
     */
    private final AtomicReference<BigInteger> lastNumber = new AtomicReference<>();
    private final AtomicReference<List<BigInteger>> lastFactors = new AtomicReference<>();

    @NotThreadSafe
    public List<BigInteger> factor(BigInteger num) {
        if (num.equals(lastNumber.get())) {
            return lastFactors.get();
        } else {
            List<BigInteger> factors = getFactor(num);

            // unsafe atomic operation
            lastFactors.set(factors);
            lastNumber.set(num);
            return factors;
            // 尽管AtomicReference的set方法具有原子特性，但在这里无法保证两个原子操作可以同时被执行完毕。
            // 也就是lastNumber和lastFactors的状态可能由于不同现成的访问造成不一致的现象
            // 要保证状态的一致性，就要在单个原子操作中更新所有相关的状态量
        }
    }

    private List<BigInteger> getFactor(BigInteger num) {
        return new ArrayList<>();
    }
}
