# 找出可利用的并行性
利用Callable和Future对象，可以实现提前执行某些耗时的任务。从而减少在获取
这些耗时任务的结果时的时间消耗。使用这种技术，也可以使得CPU空闲时（如执行
耗时的IO操作），执行一些计算型任务。从而充分的利用计算机资源。

为了解决单线程任务处理方式造成在执行IO操作时CPU空闲的现象，可以通过并行
处理任务的方式来解决这个问题。

## Callable
Callable与Runnable类似，都表示任务的抽象。但是Callable提供了更好
的抽象，call方法可以放回一个值，并可能抛出异常。

Executor执行的任务分为四个阶段：**创建**、**提交**、**开始**和**完成**。
而且Executor的执行的任务也可以终止和取消。

## Future
Future表示一个任务的生命周期，并提供判断任务状态的方法，以及获取任务结果和
取消任务的方法。Future表示任务的生命周期只能前进，不能后退。当某个任务完成后，
状态将停滞在**完成**状态。

Future.get方法的行为取决于任务的状态。如果任务已经完成，则理解返回任务的
结果或者抛出一个异常；如果任务没有完成，那么get方法将阻塞到任务完成为止。

对于get方法抛出的异常，分为三种：一种是任务抛出的异常被get方法封装为
ExecutionException后抛出，可以通过该异常的getCause方法获取原始异常；
一种是InterruptedException；一种是如果任务被取消，将抛出
CancellationExecution异常。

```java
// definition of Callable
public interface Callable<V> {
    V call() throws Exception;
}

// definition of Future
public interface Future<V> {
    boolean cancel(boolean mayInterruptIfRunning);
    boolean isCancelled();
    boolean isDone();
    V get() throws InterruptedException, ExecutionException,
                   CancellationException;
    V get(long timeout, TimeUnit unit) throws InterruptedException,
                                              ExecutionException,
                                              CancellationException;
}
```

## 并行化过程的任务异构问题
1. 把不同类型的任务平均分配给每个人并不容易。尽可能的挖掘并行性只能是针对
于相似的任务，如果对异构的任务进行并行分配，反而不能提升性能。
2. 对于不同类型的任务，任务大小也会相同。如果将任务拆分带来的消耗无法由
并行执行进行弥补，那么这种并行利用并没有效果。


## CompletionServiceExecutor
CompletionService将BlockingQueue和Executor结合在一起，使用Callable
来提交任务，而将计算结果放入BlockingQueue中，使用take或poll方法获取
结果。

总之，在需要使用线程来执行任务的地方，应该优先考虑使用Executor框架。
