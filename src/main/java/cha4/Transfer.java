package cha4;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Transfer {

    private int x = 0;

    private static Lock xLock = new ReentrantLock();

    public void set(String name) {
//        xLock.lock();
        if (x < 20) {
            x++;
            System.out.println(name + " produce x: " + x);
        } else {
            System.out.println("x is full, " + name +" is trying");
        }
//        xLock.unlock();
    }

    public void use(String name) {
//        xLock.lock();
        if (x > 0) {
            System.out.println(name + " use x: " + x);
            x--;
        } else {
            System.out.println("x is empty, " + name + " is trying");
        }
//        xLock.unlock();
    }
}
