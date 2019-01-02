package cha4;

import anotation.NotThreadSafe;
import anotation.ThreadSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NotThreadSafe
public class SynchonizedList<E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<>());

    public synchronized boolean putIfAbsent(E x) {
        boolean absent = !list.contains(x);
        if (absent) {
            list.add(x);
        }
        return absent;
        // 线程不安全的原因
        // 这个同步锁并非由SynchonizedList上的锁，这里应该锁住list
    }
}

@ThreadSafe
class SnchronizedList<E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<>());

    public boolean putIfAbsent(E e) {
        synchronized (list) {
            boolean absent = !list.contains(e);
            if (absent) {
                list.add(e);
            }
            return absent;
        }
    }
}