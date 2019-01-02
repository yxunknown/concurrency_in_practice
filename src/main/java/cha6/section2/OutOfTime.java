package cha6.section2;

import java.util.Timer;
import java.util.TimerTask;

public class OutOfTime {
    public static void main(String[] args) throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(new ThrowTask(), 1);
        Thread.sleep(1000);
        // 由于第一次任务执行时，抛出的runtime异常已经将timer关闭
        // 因此这里的调用会抛出 java.lang.IllegalStateException: Timer already cancelled.
        timer.schedule(new ThrowTask(), 1);
        Thread.sleep(5000);
    }

    static class ThrowTask extends TimerTask {
        @Override
        public void run() {
            throw new RuntimeException("runtime");
        }
    }
}
