# AsyncTask原理
AsyncTask内部使用阻塞队列和线程池方法，在Android3.0后，
AsyncTask内部的任务使用SerialExecutor串行执行。
