# FutureTask
FutureTask也可用做闭锁（Future语义，表示一种抽象的
可生成的结果）。FutureTask的计算是通过Callable来实现的，
相当于一种可以有返回结果的Runnable，Callable有三种状态：
WAITING_TO_RUN, RUNNING, COMPLETED。 完成状态包含
可能可能的结束方式，包括正常结束和异常结束。完成态也是FutureTask
的终态。

Future.get方法在任务完成时会直接返回计算结果，否则阻塞到该任务
进入完成状态，然后返回结果或则抛出异常。FutureTask把计算结果
从计算线程传递到获取结果的线程，而计算结果的安全发布由FutureTask
保证。

Callable表示任务可以抛受检查的或为受检查的异常，并且任何代码都可能
抛出Error，如果Callable抛出什么异常，都会被封装成ExecutionException
异常在get方法中重新抛出。

当Get方法抛出异常时，可能是以下三种情况之一：
1. callable抛出的受检查异常
2. RuntimeException
3. Error

必须针对三种情况进行不同的处理。对于已知的受检查异常，则重新抛出
这些已知异常；对于未知异常，如果是Error，则重新抛出，如果不是
RuntimeException, 则抛出IllegalStateException表示是一个逻辑错误，
其余的都是RuntimeException。

