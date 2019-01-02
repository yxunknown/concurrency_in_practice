package lock;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Pay {
    private Lock lock;
    private double[] accounts;
    public Pay(int n, double money) {

        accounts = new double[n];
        for (int i = 0; i < n; i++) {
            accounts[i] = money;
        }

        lock = new ReentrantLock();
    }

    public void pay(int from, double money) {
        lock.lock();
        try {
            while (accounts[from] < money) {
//                wait(1000);
                /**
                 * 当余额不足以后，由于没有正确的释放锁，因此导致
                 * 保护锁一致被支付线程占有，具有排他性，转账线程无法进入
                 * 会进入死锁状态
                 */
            }
            accounts[from] -= money;
            System.out.println("account: " + from + " pay " + money + ", amount is: " + accounts[from]);
        } catch (Exception e) {
            System.out.println("pay err: " + e.getMessage());
        } finally {
            // 相关研究表明，即使发生OOM异常，finally块的代码仍然可以执行
            lock.unlock();
        }
    }

    public void transfer(int to, double money) {
        lock.lock();
        try {
            accounts[to] += money;
            System.out.println("transfer to account: " + to + ", amount is: " + accounts[to]);
        } catch (Exception e) {
            System.out.println("transfer err: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        Pay pay = new Pay(10, 59.99);
        Random random = new Random();
        Thread p = new Thread(() -> {
            while (true) {
                pay.pay(2, 15.69);
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        },"PAY");
        p.start();

        Runnable tr = () -> {
            while (true) {
                pay.transfer(2, 10.23);
                try {
                    Thread.sleep(random.nextInt(2000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread t1 = new Thread(tr, "transfer1");
        Thread t2 = new Thread(tr, "transfer2");
        t1.start();
        t2.start();
    }
}
