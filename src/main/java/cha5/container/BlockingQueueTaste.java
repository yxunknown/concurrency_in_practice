package cha5.container;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueueTaste {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> aQueue = new ArrayBlockingQueue<>(20, true);
        Produce produce1 = new Produce(aQueue, "PRODUCE1");
        Produce produce2 = new Produce(aQueue, "PRODUCE2");
        Consume consume = new Consume(aQueue, "CONSUME");
        produce1.start();
        consume.start();
        produce2.start();
        try {
            Thread.sleep(1000);
            produce2.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Produce extends Thread {
    private ArrayBlockingQueue<Integer> queue;
    private Random random = new Random(102);

    public Produce(ArrayBlockingQueue<Integer> queue, String name) {
        this.queue = queue;
        setName(name);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // put an element into queue, if queue is full, then block current thread
                Integer product = random.nextInt(200);
                queue.put(product);
                System.out.println(getName() + " produce a product: " + product);
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

class Consume extends Thread {
    private ArrayBlockingQueue<Integer> queue;

    public Consume(ArrayBlockingQueue<Integer> queue, String name) {
        this.queue = queue;
        setName(name);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // get first element of queue, if queue is empty, then block current thread
                Integer product = queue.take();
                System.out.println(getName() + " consume a product: " + product);
                sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
