package tryvolatilet;

public class VolatileTest {
    public volatile int inc = 0;

    public void increase() {
        // volatile无法保证对变量操作的原子性
        inc++;
    }

    public static void main(String[] args) {
        final VolatileTest volatileTest = new VolatileTest();
        for (int i = 0; i < 10; i++) {
            new Thread(volatileTest::increase).start();
        }

        // 如果有子线程就让出cpu资源，保证子线程全部执行完毕
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }

        System.out.println(volatileTest.inc);
    }
}
