package cha5.synchronizedutil;

public class CountDownLatch {

    public long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
        final java.util.concurrent.CountDownLatch startGate = new java.util.concurrent.CountDownLatch(1);
        final java.util.concurrent.CountDownLatch endGate = new java.util.concurrent.CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread(() -> {
               try {
                   // 等待所有线程启动后，才继续执行
                   startGate.await();
                   try {
                       // 运行任务
                       task.run();
                   } finally {
                       // 任务结束， 发送当前任务结束标记
                       endGate.countDown();
                   }
               } catch (InterruptedException e) {

               }
            });
            t.start();
        }
        long start = System.nanoTime();
        // start task
        startGate.countDown();
        // wait for all tasks
        endGate.await();
        long end = System.nanoTime();
        return end - start;
    }

    public static void main(String[] args) {
        Runnable r = () -> {
            for (int i = 0; i < 10000; i++) {
                System.out.println(Thread.currentThread().getName() + " doing " + i);
            }
        };
        try {
            CountDownLatch c = new CountDownLatch();
            System.out.println(c.timeTasks(20, r));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
