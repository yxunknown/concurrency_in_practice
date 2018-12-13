package cha4;

import anotation.ThreadSafe;

import java.util.Vector;

@ThreadSafe
public class BetterVector<T> extends Vector<T> {
    public synchronized boolean putIfAbsent(T x) {
        boolean absent = !contains(x);
        if (absent) {
            add(x);
        }
        return absent;
    }
}
