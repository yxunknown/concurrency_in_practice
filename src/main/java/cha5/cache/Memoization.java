package cha5.cache;

import anotation.GuardedBy;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Memoization<A, V> implements Computable<A, V> {

    @GuardedBy("this")
    private final Map<A, V> cache = new HashMap<>();

    private final Computable<A, V> computable;

    public Memoization(Computable<A, V> c) {
        computable = c;
    }

    public synchronized V compute(A arg) throws InterruptedException {

        // 针对整个方法进行同步控制，导致同一时间只有一个线程进入该方法进行计算操作
        // 计算是一个耗时操作，会导致其他现场的阻塞，降低吞吐量
        V result = cache.get(arg);
        if (result == null) {
            result = computable.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }

    // a better implementation of compute method is below
//    public V compute(A arg) throws InterruptedException {
//        V result = cache.get(arg);
//        if (result == null) {
//            result = computable.compute(arg);
//            synchronized (cache) {
//                cache.put(arg, result);
//            }
//        }
//        return result;
//    }
}


class Memoization1<A, V> implements Computable<A, V> {
    private final Map<A, V> map = new ConcurrentHashMap<>();

    private final Computable<A, V> computable;

    public Memoization1(Computable c) {
        computable = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        V result = map.get(arg);
        if (result == null) {
            // 该方法的问题在于，如果A线程启动了一个耗时的计算过程
            // 而B线程进入该方法后查询相同的参数时发现没有缓存
            // 此时线程B会启动重复的计算
            // 而最佳的方法是B线程等待A线程计算完毕后获取缓存结果
            result = computable.compute(arg);
            map.put(arg, result);
        }
        return result;
    }
}

// 基于FutureTask的缓存设计
class Memoization2<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();

    private final Computable<A, V> computable;

    public Memoization2(Computable<A, V> c) {
        computable = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        Future<V> future = cache.get(arg);
        if (future == null) {
            // 该实现几乎是完美的，基于ConcurrentMap实现了很强的并发性
            // 但是该方法也有一个缺陷
            // 这里的if代码块进行了先检查后执行的操作
            // 因为可能会导致在计算相同值时，多个线程到缓存为空，因此同时启动多个计算任务
            Callable<V> callable = () -> computable.compute(arg);
            FutureTask<V> ft =new FutureTask<>(callable);
            future = ft;
            cache.put(arg, ft);
            // start to compute
            ((FutureTask<V>) future).run();
        }
        try {
            // get or wait for result
            return future.get();
        } catch (ExecutionException e) {
            throw new InterruptedException(e.getMessage());
        }
    }
}

class Memoization3<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();

    private final Computable<A, V> computable;

    public Memoization3(Computable<A, V> computable) {
        this.computable = computable;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if (f == null) {
            Callable<V> c = () -> computable.compute(arg);
            FutureTask<V> ft = new FutureTask<>(c);
            // check: if current future task is not in cache, then add it into cache
            f = cache.putIfAbsent(arg, ft);
            // start to compute
            if (f == null) {
                // current ft is not in cache, so the cache return a null reference
                f = ft;
                ft.run();
            }
        }
        try {
            return f.get();
        } catch (CancellationException e) {
            // 捕获到刚添加的任务被取消或执行失败，已经任务执行时遇到runtime error
            // 则把当前的任务移出缓存
            cache.remove(arg, f);
            return compute(arg);
        } catch (ExecutionException e) {
            throw new InterruptedException(e.getMessage());
        }
    }
}


interface Computable<INPUT, OUT> {
    OUT compute(INPUT arg) throws InterruptedException;
}

class ExpensiveFunction implements Computable<String, BigInteger> {

    @Override
    public BigInteger compute(String arg) throws InterruptedException {
        return new BigInteger(arg);
    }
}
