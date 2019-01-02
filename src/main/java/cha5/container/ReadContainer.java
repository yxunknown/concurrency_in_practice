package cha5.container;

import java.util.Vector;

public class ReadContainer {
    public static void main(String[] args) {
        Vector<Integer> vector = new Vector<>(20);
        vector.add(2);
        vector.add(3);
        vector.add(4);
        vector.add(5);

        // 可能会出错
        // 其他现场可能在迭代期间修改该容器
        vector.forEach(e -> System.out.println(e));

        // 安全的迭代，但是如果容器类有大量元素的话，会导致该线程
        // 长时间占有锁，从而降低CPU的利用率
        synchronized (vector) {
            vector.forEach(e -> System.out.println(e));
        }
    }
}
