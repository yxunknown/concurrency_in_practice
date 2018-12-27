package cha5.blockorinterrupt;

import java.util.concurrent.BlockingQueue;

public class RecoverInterrupt implements Runnable {
    private BlockingQueue<Task> queue;

    public RecoverInterrupt(BlockingQueue<Task> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            // this method will raise an InterruptedException
            // so this method will be a block method
            Task t = queue.take();
            // process t
        } catch (InterruptedException e) {
            // when catch InterruptedException
            // the bad way is do nothing
            // recover interrupted status
            Thread.currentThread().interrupt();
        }
    }
}


class Task {}
