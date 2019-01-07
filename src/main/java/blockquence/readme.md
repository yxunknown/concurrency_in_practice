# 阻塞队列
阻塞队列常用于生产者-消费者模型，阻塞队列常见的两种阻塞场景有：
1. 队列为空时，消费者端的所有线程会被阻塞，直到有数据放入队列。
2. 队列满时，生产者端的所有线程会被阻塞，直到有数据被消费。

# 阻塞队列的核心方法
1. offer(object)  
表示尝试将一个对象放入队列中。如果成功则返回true，否则返回false；
该方法不会阻塞当前的进行操作的线程。
2. offer(object, timeout, timeunit)  
将一个对象尝试放入队列中，可以设定等待时间，如果规定时间内还未完成
则返回false。
3. put(object)  
将一个对象放入队列中，如果队列没有空间，则调用该方法的线程会被阻塞。
4. poll(time)  
取走位于队列首位的对象；若不能立即取出，则等待一段时间，否则返回null。
5. poll(timeout, timeunit)  
取走位于队列首位的对象；若不能立即取出，则等待一段时间。若规定时间内
没有数据可以取出，则返回null。
6. take()  
取走位于队列首位的对象，如果队列为空，则阻塞调用该方法的线程，直到队列中
有数据为止。
7. drainTo()  
一次性取走队列中的所有可用对象。

# Java中的阻塞队列类型
1. ArrayBlockingQueue
基于数组实现的有限阻塞队列，并按照FIFO的原则对元素进行排序。默认
情况下不保证线程对队列访问的公平性。公平性是指当队列可用时，可以按照
线程被阻塞的顺序访问队列。但是保证公平性会降低队列的吞吐量。
    ```
    ArrayBlockingQueue unfairQueue = new ArrayBlockingQueue(200);
    ArratBlockiungQueue fairQueue = new ArrayBlockingQueue(200, true)
    ```
2. LinkedBlockingQueue  
基于链表实现的队列，遵循FIFO原则。由于LinkedBlockingQueue对
生产者端和消费者端分别使用了不同的锁进行同步；这样的做法能够使得
生产者端和消费者端线程可以并行的访问队列，因此可以提供LinkedBlockingQueue
高效地处理并发数据的能力。
    ```$xslt
    // default capacity is Interger.MAX_VALUE
    // this way may raise oom error
    LinkedBlockingQueue infinityLinkeQueue = new LinkedBlockingQueue();
    
    // good way
    // its good to give an initial capacity
    LinkedBlockingQueue good = new LinkedBlockingQueue(initialCapacity);
    ```
3. PriorityBlockingQueue  
支持优先级的无限队列，默认采用优先级升序方式对元素进行排序。若要改变
默认的排序规则，可以重写compareTo()方法或在构造队列时传入Comparator对象。
4. DelayQueue  
基于PriorityBlockingQueue实现，一个支持时延获取的无限队列。队列
中的元素必须实现Delayed接口，创建队列时也可以指定元素的到期时间。
5. SynchronousQueue  
每个插入操作都必须等待其他线程的读取操作，相反每个读取操作也必须等待
其他线程的插入操作。该队列中实际不包含任何元素。
6. LinkedTransferQueue  
一个基于LinkedBlockingQueue且实现了Transfer接口，Transfer接口定义了
三个比较重要的方法：
    * transfer(E e)  
    若当前存在一个正在等待获取产品的消费消费线程，则将该元素传递给消费线程；
    如果没有消费线程，则将该元素放入队列尾部，并且进入阻塞状态，直到有消费线程取走该
    元素。
    * tryTransfer(E e)  
    若当前存在一个等待消费的线程，则将该元素传递给消费者；若不存在等待消费
    的消费线程，则返回false。该方法不会进入阻塞状态！
    * tryTransfer(E e, timeout, timeunit)  
    与tryTransfer(e)类似，不过可以等待一段时间后进行返回。
7. LinkedBlockingDeque  
一个基于链表结构的双向阻塞队列。由于可以在两端进行操作，
因此该列表减少了多线程同时入队和出队的竞争情况。

# 线程池
利用Executor框架，实现了对线程提交和执行的解耦。线程提交由
Runnable或者Callable实现，而线程的执行交给Executor来处理。
## ThreadPoolExecutor    
最完善的构造方法如下：
```
public ThreadPoolExecutor(
    int corePoolSize, // 核心线程数，当任务数小于该值时，则创建新的线程执行任务；否则不创建
    int maximumPoolSize, // 最大线程数
    long keepAliveTime, // 非核心线程的闲置时间，闲置时间超过该值的非核心线程会被回收
    TimeUnit unit, // 闲置时间单位
    BlockingQueue<Runnable> workQueue, // 任务队里，如果当前任务数大于corePoolSize，则会将多余的线程放该对队列
    ThreadFactory threadFactory,  // 线程工厂，为每个新建的线程设置名字
    RejectedExecutionHandler handler // 任务队里满了时的应对策略
)
// RejectedExecutorHandler的取值， 线程池的饱和策略
1. AbordPolicy，任务队列满了之后，不接受新的任务，并抛出RejectedExecutrionException。
2. CallerRunsPolicy， 调用则者所在的线程来处理任务
3. DiscardPolicy，不执行任务，并将任务删除
4. DiscardOldestPolicy， 丢弃最老的任务，并执行当前任务 
```

## 线程池处理流程
1. 提交任务后，检查是否达到核心线程数，如果没有达到则创建新的核心线程处理任务；否则进行下一步。
2. 判断任务队列是否已满。如果没满，则将任务添加到任务队列；否则执行下一步。
3. 判断线程是否达到最大线程数，如果没有则创建非核心线程处理任务，否则执行饱和策略。

## 线程池类型
1. FixedThreadPool    
可重用固定线程数线程池。创建示例：  
```java
int nThreads = 10;
ExecutorService fixedThredPool = new ThreadPoolExecutor(
    nThreads, // 核心线程数
    nThreads, // 最大线程数
    0L,  // 冗余的线程立即被终止
    TimeUnit.MILLISECONDS, 
    new LinkedBlockingQueue<Runnable>
    );
// 核心线程数和最大线程数相等，表示该线程池没有非核心线程数，而且核心线程的数量是固定的。
// 当新任务进入该线程池之后，如果没有达到核心线程数，则创建新的核心线程来处理任务。
// 若达到了核心线程数，则将任务放入队列，等待空闲线程来处理。
```
2. CachedThreadPool
CachedThreadPool线程池是一个根据需要创建线程的线程池。  
```java
ExecutorService cachedThreadPool = new ThreadPoolExecutor(
    0,  // 核心线程数
    Interger.MAX_VALUE,  // 最大线程数，
    60L, 
    TimeUnit.SECONDS,
    new SynchronousQueue<Runnable>()
    );
// 该线程池没有核心线程，但非核心线程数量很多。当空闲线程等待60s还未执行新任务时，
// 则会被回收。而任务队列使用SynchronousQueue,该队列不存储任何元素，每插入一个任务
// 都要等待一个非核心线程来执行该任务；同理，在非核心线程请求任务的时候，也要等待新的
// 任务进入队列。
```
3. SingleThreadExecutor  
使用单个线程执行任务的线程池。创建代码如下：  
```java
ExecutorService singleThreadPool = new ThreadPoolExecutor(
    1,  // core size
    1,  // max size
    0L, 
    TimeUnit.MILLSECONDS,
    new linkedBlocingQueue<Runnable>()
    )
 // 该线程池只有一个核心线程，没有非核心线程。
 // 当任务提交到该线程池后，如果没有创建核心线程，则创建一个核心线程支持该任务；
 // 否则则将任务放入队列，由核心线程依次执行。
```
4. ScheduledThreadPool
实现定时和周期性执行任务的线程池。  
```java
ExxcutorService scheduledThreadPool = new ThreadPoolExecutor(
    10,  // core size, can change to fit meet
    Integer.MAX_VALUE,
    60,  // keep alive time, can change
    TimeUnit.MILLSECONDS,
    new DelayedQueue<Runnable>()
    )
// 这里主要使用DelayedQueue来实现定时任务和周期性任务
```
