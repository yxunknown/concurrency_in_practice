# FutureTask
FutureTask也可用做闭锁（Future语义，表示一种抽象的
可生成的结果）。FutureTask的计算是通过Callable来实现的，
相当于一种可以有返回结果的Runnable，Callable有三种状态：
WAITING_TO_RUN, RUNNING, COMPLETED。 完成状态包含
可能可能的结束方式，包括正常结束和异常结束。完成态也是FutureTask
的终态。

Future.get方法
