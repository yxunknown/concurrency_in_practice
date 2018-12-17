package lock;

import java.util.Random;

public class PaySynchronized {

    private double[] accounts;

    private Object lock = new Object();

    public PaySynchronized(int n, double money) {
        accounts = new double[n];
        for (int i = 0; i < n; i++) {
            accounts[i] = money;
        }
    }

    public synchronized void pay(int from, double money) throws InterruptedException, IndexOutOfBoundsException {
        while (accounts[from] < money) {
            //
            wait();
        }
        accounts[from] -= money;
        System.out.println("account: " + from + " pay " + money + ", amount is: " + accounts[from]);
    }

    /**
     * synchronized使用的是对象的内置锁
     */
    public synchronized void transfer(int to, double money) {
        accounts[to] += money;
        System.out.println("transfer to account: " + to + ", amount is: " + accounts[to]);
        // 唤醒处于等待的支付线程
        notifyAll();
    }

    public void safeTransfer(int to, double money) {
        // 获得某个对象的锁
        synchronized (lock) {
            accounts[to] += money;
            System.out.println("transfer to account: " + to + ", amount is: " + accounts[to]);
        }
    }

    public static void main(String[] args) {
        PaySynchronized paySynchronized = new PaySynchronized(10, 59.99);
        Random random = new Random();
        Runnable pay = () -> {
            while (true) {
                try {
                    paySynchronized.pay(2, 15.99);
                    Thread.sleep(random.nextInt(1000));
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " err:" + e.getMessage());
                }
            }
        };
        Runnable transfer = () -> {
            while (true) {
                try {
                    paySynchronized.transfer(2, 10.24);
                    Thread.sleep(random.nextInt(1500));
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " err:" + e.getMessage());
                }
            }
        };
        Thread pt = new Thread(pay, "PAY T");
        Thread tt1 = new Thread(transfer, "TRANSFER1");
        Thread tt2 = new Thread(transfer, "TRANSFER2");
        pt.start();
        tt1.start();
        tt2.start();
    }
}
