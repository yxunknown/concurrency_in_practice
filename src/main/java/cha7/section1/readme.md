# 任务取消
*Java没有提供任何机制来安全地终止线程，中断也只是一个协作机制，目的是能使
一个线程终止另一个线程的当前工作（但没有任何规范要求一定要这样做）。*

如果某个操作能在正常完成之前将其置入完成状态，那么该操作就是可取消的（Cancellable）。
取消操作的原因主要有如下几个：
1. 用户请求取消
用户主动点击取消按钮。
2. 有时间限制的操作
如果某个操作在规定时间内未完成，则主动取消。
3. 程序内部事件
当某事件发生后，取消任务。
4. 错误
遇到错误后取消任务。
5. 关闭
当程序执行关闭时，其启动的操作也该被取消。

由于java没有提供安全的方法来停止线程，因此我们可以在不同的线程之间制定协同
机制，通过协同机制来取消未完成的任务。比如在多个线程间共享状态量。

## 中断
基于状态量进行协同关闭的方式，如果run方法中的代码被阻塞，那么可能导致对状态
量的检查无法进行，从而导致任务无法取消。

每个线程都有一个boolean类型的中断状态，当线程中断时，该状态为true。Thread
类提供了中断线程已经查询线程中断状态地方法。
1. interrupt： 中断目标线程
2. isInterrupted： 返回目标线程的中断状态
3. interrupted： 清楚当前线程的中断状态，并它之前的值。

Thread的sleep/wait等方法都会检查线程何时中断，并在发现中断时提前返回。
这些方法在检查到中断后执行的操作包括清楚中断状态，并抛出InterruptedException。
**调用interrupt并不代表立即停止目标线程正常执行的操作，只是传递了中断的消息。**

对中断的正确理解是它不会真正的中断一个线程，而是发出中断请求，然后由线程在下一个
合适的时刻中断自己。如wait、sleep和join等方法，都会严苛处理这种请求。

由于一些类库的阻塞方法无法响应自定义的状态量，这也是BrokenPrimeGenerator
无法正常工作的原因。

通常，中断事实现取消最合理的方式，这种方法很容易被其他类库的阻塞方法支持。

## 中断策略
中断策略是指发现中断请求时，应该做那些工作以及以多快的速度进行响应。

最合理的中断策略是某种形式的线程级取消操作和服务及取消操作：尽快退出，在
必要时进行清理，并告知所有者改线程已经退出。

如果将InterruptedException传递给调用者外还需要其他操作，那么应该在
捕获InterruptedException后恢复中断状态：
```java
Thread.currentThread().interrupt();
```

由于每个线程由自己的中断策略，因此在不知道某个中断对该线程的含义时，就不应该
直接中断该线程。

## 响应中断
当调用一些可阻塞的方法时（会抛出InterruptedException），有两种实用的
策略可以处理InterruptedException异常：
1. 传递异常
执行一些清理操作后重新抛出InterruptedException异常，这使得该方法也变为
可阻塞的。
2. 恢复中断状态
通过Thread.currentThread().interrupt()方法，从而使调用栈的上层可以
处理中断。

但要注意，只有实现了中断策略的代码才可以屏蔽中断请求，在常规任务和库代码中
都可以正确的响应中断。

## 定时任务的实现
1. 通过中断
在单独的中断线程里调用任务线程的interrupt方法，实现关闭任务。为了达到
定时执行任务的目的，可以理解为让任务保持执行，而定时去关闭该任务。则此时
可以通过ScheduledThreadPool来实现对任务的定时关闭，将关闭任务的代码
提交给ScheduledThreadPool来执行即可。
2. 通过Future实现
因为Future定义了任务的生命周期，因此可以通过Future来关闭任务的执行。
Future.cancel(mayInterruptIfRunning)方法用来关闭任务，如果把
mayInterruptIfRunning设为true，则表示如果当前任务正在某个线程中执行，
则中断表示该线程可被中断；如果为false，表示如果任务还没启动，则不启动该
任务。由于在不清楚某线程中断策略的情况下，不应该直接中断该线程。因此cancel
方法的mayInterruptIfRunning参数只有当任务标准的Executor中执行的时候，
才可以设置为true。

## 处理不可中断的阻塞
对于那些执行不可中断而被阻塞的线程，也可以使用类似中断的技术来停止这些线程，
但前提是直到导致线程阻塞的原因：
1. java.io包中的同步Socket I/O
最常见的阻塞I/O形式就是对套接字进行读取和写入，虽然InputStream和OutputStream
中的read和write方法不会响应中断，但是通过关闭底层的套接字，可以使由于执行
read和write方法的线程捕获到一个SocketException。
2. java.io包中的同步I/O
当中断一个正在InterruptibleChannel上的等待线程时，将抛出
ClosedByInterruptException异常并关闭链路。
3. Selector的异步I/O
如果在一个线程上调用Selector.selector方法（如在java.io.channel上）
时阻塞了，那么调用close或wakeup方法时会抛出ClosedSelectorException
并提前返回。
4. 获取某个锁
如果一个线程由于等待某个内置锁而阻塞，那么将无法响应中断。因为线程认为它肯定
会获得锁，所以不会处理中断请求。但是，Lock类的lockInterruptibly方法允许
线程在等待某个锁时仍能响应中断。

## 封装非标准取消
1. 通过封装Thread实现
2. 通过Future实现
利用Future的cancel方法，实现对任务的取消。

