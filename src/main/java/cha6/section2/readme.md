# Executor框架
串行方式严重影响了系统的吞吐量和响应性，而每个任务一个线程的方式也带来了资源管理
的复杂性。可以通过有界队列来避免高负荷的应用程序耗尽内存，而Executor框架简化了
这个工作，Executor将任务抽象到Executor对象上，而不是Thread上，分离了线程的创建
和任务的执行操作。

```java
/**
* executor interface
*/
public interface Executor {
    void execute(Runnable command);
}
```
这个简单的接口为强大的异步任务执行框架提供了基础，该接口也提供了一种标准的方法
将任务的提交和任务的执行解耦开来，用Runnable子类来表示任务。

Executor基于生产者-消费者模型，提交任务的操作相当于生产者，执行任务线程相当于
消费者。

## 执行策略
通过将任务的提交和执行解耦开来，可以很轻易地为每个任务指定和修改执行策略。
执行策略定义了任务执行的What,Where,When,How等几方面的定义：
1. 在什么线程中执行任务
2. 按照什么顺序执行（FIFO, FILO, 优先级）
3. 有多少个任务可以并发执行
4. 有多少个任务处理等待
5. 当系统过载时，如何选择被拒绝的任务，已经如何通知应用程序被拒绝的任务
6. 在任务执行时，执行那些操作

不同的执行策略代表不同的资源管理方式，最佳的策略由可用的资源和应用程序对服务
质量的要求来共同决定。
但无论如何，都应该避免直接创建线程执行任务。

## 线程池
线程池是一组同构工作线程的资源池。

线程池的优势在于通过重用现有线程从而提高每个任务的响应性，也可以分担创建线程
带来的开销；此外，当任务到达线程池时，线程往往已经创建完毕，因此任务不会等待
创建线程所需要的时间；而通过适当调整线程池大小，可以创建足量的线程使CPU保持忙碌，
但又不会导致大量的线程竞争CPU资源。

Java提供了一些默认的线程池配置，可以通过Executors的静态方法来创建不同类型的
线程池：
1. newFixedThreadPool
FixedThreadPool是一个固定长度的线程池，每提交一个任务就创建一个线程，直到
到达线程池的最大线程数，此时线程池规模不在继续扩大。
2. newCachedThreadPool
CacheThreadPool是一个可缓存的线程池，如果线程池当前的规模超过了处理需求，
则回收空闲线程；而当需求增加时，则会添加新的线程。这类线程池的规模没有限制。
3. newSingleThreadExecutor
SingleThreadExecutor是一个单线程的Executor，创建单个工作线程来处理任务。
这类线程池可以保证任务队列中的任务可以按照某种顺序（FIFO, LIFO, 优先级）执行。
4. newScheduledThreadPool
ScheduledThreadPool是一个固定长度的线程池，但是可以延迟或定时执行任务。

将为每个任务新建一个线程变为线程池的策略，使得系统不会在高负载的情况下瘫痪，
也可以轻易地实现调优、管理、监视、记录日志、错误报告等功能。

## Executor生命周期
由于JVM只有在所有的（非守护）线程全部终止后才会退出，因此如果无法正确的
关闭Executor，则会导致JVM无法结束。

ExecutorService接口定义了Executor的生命周期：
```java
public interface ExecutorService {
    void shutdown();
    List<Runnable> shutdownNow();
    boolean isShutdown();
    boolean isTerminated();
    boolean awaitTermination(long timeout, TimeUnit unit);
}
```

ExecutorService的生命周期由三个状态：运行、关闭和已终止。在初始创建期属于
运行状态；shutdown属于平滑关闭过程，不在接受新任务，等待所有已提交的任务执行
完毕；shutdownNow属于暴力关闭过程，它尝试取消所有正在运行的任务，也不会继续
执行队列中未启动的任务。

RejectedExecutionHandle负责关闭后拒绝新提交的任务，对于新提交的任务，会在
execute方法中抛出RejectedExecutionException。所有任务完成后，ExecutorService
会进入终止状态。可以调用awaitTermination等待达到终止态，或调用isTerminated来
轮询。在调用awaitTermination方法后立即调用shutdown方法，可以实现同步关闭
ExecutorService的效果。

## 延迟任务与周期任务
Timer类负责管理延时任务（延迟一段时间后执行某任务）以及周期性任务（每隔多长时间
重复执行某任务）。但是Timer类有一些缺陷（Timer内部只有一个线程来执行任务，如果
任务的执行时间超过定时任务的间隔时间，就会导致下一次任务的执行时间被延后，也就是
Timer类无法保证每次任务的精准性），因此可以使用ScheduledThreadPoolExecutor
来解决定时任务的问题。

## Timer的缺陷
1. 单线程处理任务，无法保证每次任务精准的定时调用。
2. Timer类不捕获内部任务执行线程TimerTask抛出的异常，且Timer也不会恢复
线程的执行，而是将整个Timer取消。已经调度而未执行的任务不会被执行，新的任务
不能被调度的现象称为线程泄露（Thread Leakage）
