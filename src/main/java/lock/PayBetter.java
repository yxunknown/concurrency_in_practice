package lock;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PayBetter {
    private Lock lock;
    private double[] accounts;
    private Condition payCondition;
    public PayBetter(int n, double money) {

        accounts = new double[n];
        for (int i = 0; i < n; i++) {
            accounts[i] = money;
        }

        lock = new ReentrantLock();
        payCondition = lock.newCondition();
    }

    public void pay(int from, double money) throws InterruptedException {
        lock.lock();
        try {
            while (accounts[from] < money) {
                // 阻塞当前线程，并放弃锁
                /**
                 * 发现余额不足后，当前支付线程立即将将自己阻塞，等待转账线程完成转账后唤醒当前支付线程
                 */
                // 这里会无限等待，直到其他地方的condition对象调用了signalAll或signal方法
                // 唤醒的线程会进入runnable状态，等待调度器进行调度，而非直接进入running状态
                payCondition.await();
            }
            accounts[from] -= money;
            System.out.println("account: " + from + " pay " + money + ", amount is: " + accounts[from]);
        } catch (Exception e) {
            System.out.println("pay err: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void transfer(int to, double money) {
        lock.lock();
        try {
            accounts[to] += money;
            System.out.println("transfer to account: " + to + ", amount is: " + accounts[to]);
            // 激活等待转账的线程
            payCondition.signalAll();
        } catch (Exception e) {
            System.out.println("transfer err: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        PayBetter pay = new PayBetter(10, 59.99);
        Random random = new Random();
        Thread py = new Thread(() -> {
            while (true) {
                try {
                    pay.pay(2, 15.69);
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        },"PAY");
        py.start();

        Runnable trns = () -> {
            while (true) {
                pay.transfer(2, 11.23);
                try {
                    Thread.sleep(random.nextInt(2000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread t1 = new Thread(trns, "transfer1");
        Thread t2 = new Thread(trns, "transfer2");
        t1.start();
        t2.start();
    }
}
